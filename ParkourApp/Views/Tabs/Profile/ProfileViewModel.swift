import Foundation
import SwiftUICore
import Combine
import UIKit

class ProfileViewModel: ObservableObject {
    var cancel = Set<AnyCancellable>()
    @Published var user: User?
    @Published var spots: [Spot] = []
    @Published var isLoading = true
    @Published var showCreateSpotSheet: Bool = false
    @Published var showUsernameAsTitle: Bool = false
    @Published var showSettings: Bool = false
    var isOwnProfile: Bool
    private var colorScheme: ColorScheme
    
    init(username: String, colorScheme: ColorScheme) {
        self.isOwnProfile = OWN_USER.username == username
        self.colorScheme = colorScheme
        self.loadUser(username: username)
        self.getSpots(username: username)
    }
    
    func loadUser(username: String) {
        let publisher = Service().getUser(username: username)
        
        publisher.sink { error in
            
        } receiveValue: { data in
            self.user = data
            self.isLoading = false
        }.store(in: &cancel)
    }
    
    func getProfilePicture() -> URL {
        return Service().getProfilePicture(username: user!.username, colorScheme: colorScheme)
    }
    
    func getSpots(username: String) {
        let publisher = Service().getUserSpots(username: username)
        
        publisher.sink { error in
            
        } receiveValue: { data in
            self.spots = data.spots
        }.store(in: &cancel)
    }
    
    func getSpotImage(id: Int8) -> URL {
        return Service().getSpotPicture(id: id)
    }
    
    func getSpotLowResImage(id: Int8) -> URL {
        return Service().getLowResSpotPicture(id: id)
    }
    
    func getScreenWidth() -> CGFloat {
        return UIScreen.main.bounds.size.width / 3.0 - 20.0
    }
}
