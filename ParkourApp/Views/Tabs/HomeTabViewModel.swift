import Foundation
import Combine

class HomeTabViewModel: ObservableObject {
    var cancel = Set<AnyCancellable>()
    @Published var user: User = User(username: "", description: "", spotsAmount: 0, follower: 0, follows: 0)
    
    init() {
        let publisher = Service().getOwnUser()
        
        publisher.sink { error in
            print("GetOwnUser: \(error)")
        } receiveValue: { data in
            print("TESTING: \(data)")
            self.user = data
        }.store(in: &cancel)
    }
}
