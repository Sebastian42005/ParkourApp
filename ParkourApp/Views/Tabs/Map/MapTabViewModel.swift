import Foundation
import CoreLocation
import MapKit
import Combine
import SwiftUI

class MapTabViewModel: NSObject, ObservableObject, CLLocationManagerDelegate, TabViewModelProtocol {
    
    // MARK: - Properties
    private var cancel = Set<AnyCancellable>()
    private var locationManager: CLLocationManager?
    
    // Map & Marker
    @Published var spots: [MarkerDto] = []
    @Published var cameraPosition: MapCameraPosition = .automatic
    @Published var selectedItemId: Int? {
        didSet {
            if let id = selectedItemId {
                selectedMarker = spots.first { $0.spotId == id }
                if let selectedMarker = selectedMarker {
                    loadSpot(spotId: selectedMarker.spotId)
                    setCameraPosition(marker: selectedMarker)
                    showInfoSheet = true
                }
            } else {
                selectedMarker = nil
            }
        }
    }
    var selectedMarker: MarkerDto?
    
    // InfoSheet & Navigation
    @Published var showInfoSheet: Bool = false
    @Published var currentDetent: PresentationDetent = .medium
    @Published var selectedSpot: Spot?
    @Published var showSpotDetails = false
    
    // Filter
    @Published var selectedSport: Sport?
    @Published var selectedAttributes: [Attribute] = []
    @Published var selectedRating: Int?
    @Published var selectedCity: String?
    @Published var showFilterSheet = false
    @Published var isFilterActive = false
    
    // MARK: - Init
    override init() {
        super.init()
        loadMarkers()
    }

    // MARK: - Map & Marker Methods
    func setCameraPosition(marker: MarkerDto) {
        let camera = MapCamera(
            centerCoordinate: CLLocationCoordinate2D(latitude: marker.latitude, longitude: marker.longitude),
            distance: 500,
            heading: 0,
            pitch: 0
        )
        withAnimation(.easeInOut(duration: 1.0)) {
            self.cameraPosition = .camera(camera)
        }
    }

    func resetSelection() {
        selectedMarker = nil
        selectedSpot = nil
        selectedItemId = nil
        currentDetent = .fraction(0.4)
    }
    
    // MARK: - Data Loading
    func loadSpot(spotId: Int) {
        Service().getSpot(id: spotId)
            .sink { completion in
                if case let .failure(error) = completion {
                    print("❌ Fehler beim Laden des Spots: \(error.localizedDescription)")
                }
            } receiveValue: { [weak self] spot in
                self?.selectedSpot = spot
            }
            .store(in: &cancel)
    }

    func loadMarkers() {
        isFilterActive = !selectedAttributes.isEmpty || selectedSport != nil || selectedRating != nil || selectedCity != nil
        Service().getMarkers(
            sport: selectedSport,
            attributes: selectedAttributes,
            rating: selectedRating,
            search: nil,
            city: selectedCity
        )
        .sink { completion in
            if case let .failure(error) = completion {
                print("❌ Fehler beim Filtern der Marker: \(error.localizedDescription)")
            }
        } receiveValue: { [weak self] filtered in
            self?.spots = filtered
        }
        .store(in: &cancel)
    }

    // MARK: - Utility
    func getSportImage(name: String) -> URL {
        Service().getSportImageUrl(name: name)
    }
}
