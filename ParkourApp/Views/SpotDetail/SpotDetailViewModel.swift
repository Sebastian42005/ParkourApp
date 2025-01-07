import Foundation
import SwiftUICore
import UIKit
import Combine

class SpotDetailViewModel: ObservableObject {
    var cancel = Set<AnyCancellable>()
    @Published var spot: Spot = Spot(id: 0, description: "", location: Location(id: 0, latitude: 0, longitude: 0, city: ""), spotTypes: [], user: "", createdAt: "", rating: 0, pictures: [])
    @Published var isLoading = true
    @Published var selectedStar: Int = 0
    
    init(spotId: Int) {
        self.loadSpot(spotId: spotId)
    }
    
    func getSpotImage(id: Int8) -> URL {
        return Service().getSpotPicture(id: id)
    }
    
    func loadSpot(spotId: Int) {
        let publisher = Service().getSpot(id: spotId)
        
        publisher.sink { error in
            print("Get Spot: \(error)")
            
        } receiveValue: { data in
            self.spot = data
            self.isLoading = false
        }.store(in: &cancel)
    }
    
    func getScreenWidth() -> CGFloat {
        return UIScreen.main.bounds.size.width - 20
    }
}
