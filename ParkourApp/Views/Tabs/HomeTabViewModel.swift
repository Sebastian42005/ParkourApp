import Foundation
import Combine

class HomeTabViewModel: ObservableObject {
    var cancel = Set<AnyCancellable>()
    @Published var user: User = User(username: "", description: "", spotsAmount: 0, follower: 0, follows: 0, isFollowed: false, averageRating: 0)
    var viewHandler: ViewHandler;

    init(viewHandler: ViewHandler) {
        self.viewHandler = viewHandler
        let publisher = Service().getOwnUser()
        
        publisher.sink { error in
        } receiveValue: { data in
            self.user = data
        }.store(in: &cancel)
    }
}
