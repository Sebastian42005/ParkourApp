import Foundation
import SwiftUICore
import UIKit
import Combine

class SpotDetailViewModel: ObservableObject {
    var cancel = Set<AnyCancellable>()
    @Published var spot: Spot = Spot(id: 0, title: "Test Titel", description: "", location: Location(id: 0, latitude: 0, longitude: 0, city: ""), sport: "", attributes: [], user: "", createdAt: "", rating: 0, pictures: [])
    @Published var isLoading = true
    @Published var selectedStar: Int = 0
    @Published var ratingDescription: String = ""
    @Published var isPopupPresented = false
    @Published var ratings: [RatingDto]?
    
    init(spotId: Int) {
        self.loadSpot(spotId: spotId)
        self.loadRatings(spotId: spotId)
    }
    
    func loadRatings(spotId: Int) {
        let publisher = Service().getSpotRatings(id: spotId)

        publisher.sink { error in
        } receiveValue: { data in
            self.ratings = data
        }.store(in: &cancel)
    }
    
    func getSpotLowResImage(id: Int8) -> URL {
        return Service().getLowResSpotPicture(id: id)
    }
    
    func getSpotImage(id: Int8) -> URL {
        return Service().getSpotPicture(id: id)
    }
    
    func getAttributeImage(id: Int) -> URL {
        return Service().getAttributesImageUrl(id: id)
    }
    
    func loadSpot(spotId: Int) {
        let publisher = Service().getSpot(id: spotId)
        
        publisher.sink { error in
            
        } receiveValue: { data in
            self.spot = data
            self.isLoading = false
        }.store(in: &cancel)
    }
        
    func getScreenWidth() -> CGFloat {
        return UIScreen.main.bounds.size.width - 20
    }
    
    func getScreenHeight() -> CGFloat {
        return UIScreen.main.bounds.size.height
    }
}
