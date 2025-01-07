import Foundation
import SwiftUI
import Combine

class RegisterViewModel: ObservableObject {
    var cancel = Set<AnyCancellable>()
    @Published var email: String = ""
    @Published var username: String = ""
    @Published var password: String = ""
    @Published var confirmPassword: String = ""
    @Published var errorToast: FancyToast? = nil
    @Published var isLoading = false
    @Published var loginState = LoginState.none
    var viewHandler: ViewHandler;
    
    init(viewHandler: ViewHandler) {
        self.viewHandler = viewHandler
        self.checkIfUserIsLoggedIn()
    }
    
    func buttonClick() {
        switch loginState {
        case .none:
            checkLoginState()
        case .login:
            login()
        case .register:
            register()
        }
    }
    
    func checkLoginState() {
        if (username.isEmpty) {
            self.errorToast = FancyToast(type: .error, title: "Login", message: "Username can't be empty")
            return
        }
        let publisher = Service().checkLogin(username: username)
        
        publisher.sink { error in
            print("CheckLoginState: \(error)")
        } receiveValue: { data in
            if data.loggedIn {
                self.loginState = .login
            } else {
                self.loginState = .register
            }
        }.store(in: &cancel)
    }
    
    func login() {
        self.isLoading = true
        let publisher = Service().login(username: username, password: password)
        
        publisher.sink { error in
            self.errorToast = FancyToast(type: .error, title: "Login", message: "Wrong Username or password")
        } receiveValue: { token in
            if (token.token != nil) {
                UserDefaults.standard.set(token.token, forKey: "token")
                user = token.user!
                self.viewHandler.page = .tabview
            }else {
                self.errorToast = FancyToast(type: .error, title: "Login", message: "Wrong username or password")
            }
            self.isLoading = false
        }.store(in: &cancel)

    }
    
    func register() {
        if password == confirmPassword {
            self.isLoading = true
            let publisher = Service().register(username: username, email: email, password: password)
            
            publisher.sink { error in
                print("Register: \(error)")
            } receiveValue: { data in
                self.loginState = .login
                self.errorToast = FancyToast(type: .success, title: "Register", message: "Successfully registered")
                self.isLoading = false
            }.store(in: &cancel)
        } else {
            self.errorToast = FancyToast(type: .error, title: "Register", message: "Password don't match")
        }
    }
    
    func checkIfUserIsLoggedIn() {
        let publisher = Service().getOwnUser()
        
        publisher.sink { error in
        } receiveValue: { databaseUser in
            self.viewHandler.page = .tabview
            user = databaseUser
        }.store(in: &cancel)
    }
}
