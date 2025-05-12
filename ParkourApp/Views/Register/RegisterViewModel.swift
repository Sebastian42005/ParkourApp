import Foundation
import SwiftUI
import Combine

class RegisterViewModel: ObservableObject {
    var cancel = Set<AnyCancellable>()
    @Published var email: String = ""
    @Published var username: String = ""
    @Published var password: String = ""
    @Published var confirmPassword: String = ""
    @Published var isLoading = false
    @Published var loginState: LoginState = .login

    @Published var registerStep: Int = 1
    @Published var selectedSports: [Sport] = []
    @Published var allSports: [Sport] = []
    @Published var searchSportText: String = ""

    var filteredSports: [Sport] {
        if searchSportText.isEmpty {
            return allSports
        } else {
            return allSports.filter { $0.name.localizedCaseInsensitiveContains(searchSportText) }
        }
    }

    var viewHandler: ViewHandler

    init(viewHandler: ViewHandler) {
        self.viewHandler = viewHandler
        self.checkIfUserIsLoggedIn()
        self.loadAllSports()
    }

    func buttonClick() {
        switch loginState {
        case .login:
            login()
        case .register:
            if registerStep == 1 {
                registerStep = 2
            } else {
                register()
            }
        }
    }

    func loadAllSports() {
        Service().getSports()
            .sink { _ in } receiveValue: { sports in
                self.allSports = sports
            }
            .store(in: &cancel)
    }

    func login() {
        self.isLoading = true
        Service().login(username: username, password: password)
            .sink { completion in
                switch completion {
                case .failure:
                    ToastManager.shared.showToast(
                        type: .error,
                        title: "login".localized(),
                        message: "wrong_username_password".localized()
                    )
                    self.isLoading = false
                case .finished:
                    break
                }
            } receiveValue: { token in
                if let authToken = token.token {
                    UserDefaults.standard.set(authToken, forKey: "token")
                    OWN_USER = token.user!
                    ToastManager.shared.showToast(
                        type: .success,
                        title: "login".localized(),
                        message: "successfully_logged_in".localized()
                    )
                    self.viewHandler.page = .tabview
                } else {
                    ToastManager.shared.showToast(
                        type: .error,
                        title: "login".localized(),
                        message: "wrong_username_password".localized()
                    )
                }
                self.isLoading = false
            }
            .store(in: &cancel)
    }

    func register() {
        if password == confirmPassword {
            self.isLoading = true
            let publisher = Service().register(
                username: username,
                email: email,
                password: password,
                favoriteSports: selectedSports.map { $0.name }
            )

            publisher
                .sink { completion in
                    switch completion {
                    case .failure(let error):
                        ToastManager.shared.showToast(
                            type: .error,
                            title: "register".localized(),
                            message: String(format: "registration_error".localized(), error.localizedDescription)
                        )
                    case .finished:
                        break
                    }
                } receiveValue: { _ in
                    self.loginState = .login
                    ToastManager.shared.showToast(
                        type: .success,
                        title: "register".localized(),
                        message: "successfully_logged_in".localized()
                    )
                    self.isLoading = false
                }
                .store(in: &cancel)
        } else {
            ToastManager.shared.showToast(
                type: .error,
                title: "register".localized(),
                message: "password_mismatch".localized()
            )
        }
    }

    var isFormValid: Bool {
        if loginState == .login {
            return !username.isEmpty && !password.isEmpty
        } else {
            if registerStep == 1 {
                return !username.isEmpty && !email.isEmpty && !password.isEmpty && !confirmPassword.isEmpty && password == confirmPassword
            } else {
                return !selectedSports.isEmpty
            }
        }
    }

    func checkIfUserIsLoggedIn() {
        Service().getOwnUser()
            .sink { _ in } receiveValue: { databaseUser in
                self.viewHandler.page = .tabview
                OWN_USER = databaseUser
            }
            .store(in: &cancel)
    }
}
