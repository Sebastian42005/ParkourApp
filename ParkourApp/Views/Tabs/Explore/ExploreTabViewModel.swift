import Foundation
import UIKit
import SwiftUICore
import Combine

class ExploreTabViewModel: ObservableObject, TabViewModelProtocol {
    var cancel = Set<AnyCancellable>()
    @Published var searchText = ""
    var lastSearchText: String = ""
    var lastUserSearchText: String = ""
    @Published var selectedType: SearchType = .spots
    @Published var isFilterSheetActive: Bool = false
    @Published var isLoading = true
    
    @Published var selectedSport: Sport?
    @Published var selectedAttributes: [Attribute] = []
    @Published var selectedRating: Int?
    @Published var selectedCity: String?
    @Published var showFilterSheet = false
    @Published var isFilterActive = false
    
    @Published var selectedUsername: String? = nil

    var spots: [Spot] = []
    var users: [String] = []
    
    init() {
        loadSpots()
        loadUsers()
    }
    
    func performSearch() {
        if selectedType == .spots {
            if lastSearchText != searchText {
                loadSpots()
            }
        } else {
            if lastUserSearchText != searchText {
                loadUsers()
            }
        }
    }
    
    func loadUsers() {
        lastUserSearchText = searchText
        self.isLoading = true
        let publisher = Service().searchUsers(search: searchText)
        
        publisher.sink { error in
            self.isLoading = false
        } receiveValue: { data in
            self.users = data
        }.store(in: &cancel)
    }
    
    func loadSpots() {
        lastSearchText = searchText
        self.isLoading = true
        isFilterActive = !selectedAttributes.isEmpty || selectedSport != nil || selectedRating != nil || selectedCity != nil
        let publisher = Service().searchSpots(sport: selectedSport, attributes: selectedAttributes, rating: selectedRating, search: searchText, city: selectedCity)
        
        publisher.sink { error in
            self.isLoading = false
        } receiveValue: { data in
            self.spots = data.spots
        }.store(in: &cancel)
    }
    
    func getSpotImage(id: Int8) -> URL {
        return Service().getSpotPicture(id: id)
    }
    
    func getLowResSpotImage(id: Int8) -> URL {
        return Service().getLowResSpotPicture(id: id)
    }
    
    func getScreenWidth() -> CGFloat {
        return UIScreen.main.bounds.size.width / 3.0 - 20.0
    }
}
