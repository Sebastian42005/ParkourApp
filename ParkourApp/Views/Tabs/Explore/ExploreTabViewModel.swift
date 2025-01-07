import Foundation
import UIKit
import SwiftUICore
import Combine

class ExploreTabViewModel: ObservableObject {
    var cancel = Set<AnyCancellable>()
    @Published var searchText = ""
    @Published var isLoading = true

    var spots: [Spot] = []
    
    init() {
        loadSpots()
    }
    
    func loadSpots() {
        self.isLoading = true
        let publisher = Service().getAllSpots(search: searchText, spotType: "")
        
        publisher.sink { error in
            print("Get All Spots: \(error)")
            
        } receiveValue: { data in
            self.spots = data.spots
            self.isLoading = false
        }.store(in: &cancel)
    }
    
    func getSpotImage(id: Int8) -> URL {
        return Service().getSpotPicture(id: id)
    }
    
    func getScreenWidth() -> CGFloat {
        return UIScreen.main.bounds.size.width / 3.0 - 20.0
    }
}
