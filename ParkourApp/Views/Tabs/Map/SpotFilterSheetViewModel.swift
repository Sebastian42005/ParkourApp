import SwiftUI
import Combine

class SpotFilterSheetViewModel: ObservableObject {
    var cancel = Set<AnyCancellable>()
    @Published var sports: [Sport] = []
    @Published var attributes: [Attribute] = []
    @Published var selectedAttributes: Set<Attribute>
    @Published var selectedSport: Sport? {
        didSet {
            selectedAttributes.removeAll()
            attributes = selectedSport?.attributes ?? []
        }
    }
    @Published var selectedRating: Int? = nil
    @Published var cities: [String] = []
    @Published var selectedCity: String?
   
    var mapViewModel: TabViewModelProtocol?

    init(viewModel: TabViewModelProtocol) {
        self.mapViewModel = viewModel
        self.selectedSport = viewModel.selectedSport
        self.selectedAttributes = Set(viewModel.selectedAttributes)
        self.selectedCity = viewModel.selectedCity
        self.selectedRating = viewModel.selectedRating
        loadSports()
        loadCities()
    }

    func applyFilter() {
        mapViewModel?.selectedSport = selectedSport
        mapViewModel?.selectedAttributes = Array(selectedAttributes)
        mapViewModel?.showFilterSheet = false
        mapViewModel?.selectedRating = selectedRating
        mapViewModel?.selectedCity = selectedCity
    }

    func loadSports() {
        Service().getSports()
            .sink { completion in
                if case let .failure(error) = completion {
                    print("⚠️ Fehler beim Laden der Sportarten: \(error.localizedDescription)")
                }
            } receiveValue: { [weak self] sports in
                self?.sports = sports
            }
            .store(in: &cancel)
    }
    
    func resetFilters() {
        self.selectedCity = nil
        self.selectedSport = nil
        self.selectedRating = nil
        self.selectedAttributes.removeAll()
    }
    
    func loadCities() {
        Service().getAllCities()
            .sink { completion in
                if case let .failure(error) = completion {
                    print("⚠️ Fehler beim Laden der Sportarten: \(error.localizedDescription)")
                }
            } receiveValue: { [weak self] cities in
                self?.cities = cities
            }
            .store(in: &cancel)
    }
}
