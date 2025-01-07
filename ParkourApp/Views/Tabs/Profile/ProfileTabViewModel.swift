import Foundation
import UIKit
import SwiftUICore
import Combine

class ProfileTabViewModel: ObservableObject {
    var cancel = Set<AnyCancellable>()
    @Published var user: User
    @Published var spots: [Spot] = []
    @Published var isLoading = true
    var colorScheme: ColorScheme
    
    init(user: User, colorScheme: ColorScheme) {
        self.user = user
        self.colorScheme = colorScheme
        self.getSpots()
    }
    
    func getProfilePicture() -> URL {
        return Service().getOwnProfilePicture(colorScheme: colorScheme)
    }
    
    func getSpots() {
        if user.username != nil && !user.username.isEmpty {
            let publisher = Service().getUserSpots(username: user.username)
            
            publisher.sink { error in
                print("Get User Spots: \(error)")
                
            } receiveValue: { data in
                self.spots = data.spots
                self.isLoading = false
            }.store(in: &cancel)
        }
    }
    
    func getSpotImage(id: Int8) -> URL {
        return Service().getSpotPicture(id: id)
    }
    
    func getScreenWidth() -> CGFloat {
        return UIScreen.main.bounds.size.width / 3.0 - 20.0
    }
}
