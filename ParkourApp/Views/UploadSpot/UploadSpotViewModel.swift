import SwiftUI
import Combine
import CoreLocation

class UploadSpotViewModel: NSObject, ObservableObject, CLLocationManagerDelegate {
    // MARK: - Published Properties
    @Published var capturedImages: [UIImage] = []
    @Published var title: String = ""
    @Published var spotDescription: String = ""
    @Published var selectedAttributes: Set<Attribute> = []
    @Published var sportOptions: [Sport] = []
    @Published var sportAttributes: [Attribute] = []

    @Published var location: CLLocationCoordinate2D? {
        didSet {
            updateAddressText(from: location)
        }
    }
    @Published var locationPickerText: String = "Pick Location"
    @Published var city: String = ""

    @Published var isUploading: Bool = false
    @Published var showLocationPicker: Bool = false

    @Published var selectedSport: Sport? {
        didSet {
            selectedAttributes.removeAll()
            sportAttributes = selectedSport?.attributes ?? []
        }
    }

    // MARK: - Private Properties
    private var locationManager = CLLocationManager()
    private var currentGPSLocation: CLLocationCoordinate2D?
    private var hasSetInitialLocation = false
    private var cancel = Set<AnyCancellable>()

    var showView: Binding<Bool>

    // MARK: - Init
    init(showView: Binding<Bool>) {
        self.showView = showView
        super.init()
        loadSports()
        setupLocationManager()
    }

    // MARK: - Location Management
    private func setupLocationManager() {
        locationManager.delegate = self
        locationManager.desiredAccuracy = kCLLocationAccuracyBest
        locationManager.requestWhenInUseAuthorization()
        locationManager.startUpdatingLocation()
    }

    func locationManager(_ manager: CLLocationManager, didUpdateLocations locations: [CLLocation]) {
        guard let latest = locations.last else { return }
        let coord = latest.coordinate
        currentGPSLocation = coord

        if !hasSetInitialLocation {
            hasSetInitialLocation = true
            location = coord
        }

        fetchCity(from: latest)
    }

    func locationManager(_ manager: CLLocationManager, didFailWithError error: Error) {
        print("📍 Fehler beim Abrufen der Standortdaten: \(error.localizedDescription)")
    }

    private func fetchCity(from location: CLLocation) {
        CLGeocoder().reverseGeocodeLocation(location) { placemarks, error in
            if let error = error {
                print("📍 Fehler beim Abrufen der Stadt: \(error.localizedDescription)")
                return
            }

            if let city = placemarks?.first?.locality {
                DispatchQueue.main.async {
                    self.city = city
                }
            }
        }
    }

    private func updateAddressText(from coord: CLLocationCoordinate2D?) {
        guard let coord = coord else {
            self.locationPickerText = "Pick Location"
            print("Coordinate is nil")
            return
        }

        print("📍 Calling getAddress with coord: \(coord)")

        coord.getAddress { address in
            if let address = address {
                print("📍 Address found: \(address)")

                DispatchQueue.main.async {
                    self.locationPickerText = address.street ?? address.city ?? address.fullAddress
                    self.city = address.city ?? ""
                }
            } else {
                print("No address found for coordinates: \(coord)")
                DispatchQueue.main.async {
                    self.locationPickerText = "Address not found"
                    self.city = ""
                }
            }
        }
    }


    // MARK: - Sports
    func loadSports() {
        Service().getSports()
            .sink { completion in
                if case let .failure(error) = completion {
                    print("⚠️ Fehler beim Laden der Sportarten: \(error.localizedDescription)")
                }
            } receiveValue: { [weak self] sports in
                self?.sportOptions = sports
            }
            .store(in: &cancel)
    }

    // MARK: - UI Interaction
    func addImage(_ image: UIImage) {
        capturedImages.append(image)
    }

    func removeImage(at index: Int) {
        guard capturedImages.indices.contains(index) else { return }
        capturedImages.remove(at: index)
    }

    func goBack() {
        showView.wrappedValue = false
    }

    // MARK: - Upload
    func uploadSpot() {
        guard let location = location else {
            ToastManager.shared.showToast(type: .error, title: "Fehler", message: "Bitte wähle einen Standort aus.")
            return
        }

        isUploading = true

        let spot = SpotRequest(
            title: title,
            description: spotDescription,
            latitude: location.latitude,
            longitude: location.longitude,
            city: city,
            attributes: selectedAttributes.map { $0.name },
            sport: selectedSport?.name ?? ""
        )

        Service().uploadSpot(spot: spot)
            .sink { [weak self] completion in
                if case .failure(_) = completion {
                    ToastManager.shared.showToast(type: .error, title: "Upload Spot", message: "Es gab ein Problem beim Hochladen.")
                    self?.isUploading = false
                }
            } receiveValue: { [weak self] response in
                self?.uploadImages(spotId: response.id)
            }
            .store(in: &cancel)
    }

    private func uploadImages(spotId: Int) {
        Service().uploadSpotImages(spotId: spotId, images: capturedImages)
            .sink { [weak self] completion in
                if case .failure(_) = completion {
                    ToastManager.shared.showToast(type: .error, title: "Upload Bilder", message: "Die Bilder konnten nicht hochgeladen werden.")
                    self?.isUploading = false
                }
            } receiveValue: { [weak self] _ in
                ToastManager.shared.showToast(type: .success, title: "Erfolg", message: "Spot erfolgreich hochgeladen!")
                self?.goBack()
            }
            .store(in: &cancel)
    }
}
