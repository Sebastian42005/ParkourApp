import Foundation
import Combine

class ViewHandler: ObservableObject {
    @Published var page: CurrentPage = .empty
    var cancel = Set<AnyCancellable>()
    
    init() {
        let token = UserDefaults.standard.string(forKey: "token")
        let publisher = Service().verifyToken()
        
        publisher.sink { error in
            print("VerifyToken: \(error)")
        } receiveValue: { isVerifyed in
            self.page = isVerifyed.verified ? .tabview : .login
            if isVerifyed.user != nil {
                OWN_USER = isVerifyed.user!
            }
            
        }.store(in: &cancel)
    }
    
    
}

enum CurrentPage{
    case empty
    case login
    case tabview
}
