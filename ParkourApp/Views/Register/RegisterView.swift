import SwiftUI

struct RegisterView: View {
    @ObservedObject var viewModel: RegisterViewModel
    var body: some View {
        if !viewModel.isLoading {
            VStack(spacing: 15) {
                Spacer()
                Text("Spot Finder")
                    .font(.title)
                    .fontWeight(.bold)
                    .foregroundColor(Color.primary)
                    .padding(.bottom, 20)
                CustomTextField(text: $viewModel.username, placeHolder: "Username")
                if viewModel.loginState == LoginState.register {
                    CustomTextField(text: $viewModel.email, placeHolder: "Email")
                    
                }
                if viewModel.loginState != LoginState.none {
                    CustomTextField(text: $viewModel.password, placeHolder: "Password", secure: true)
                }
                if viewModel.loginState == LoginState.register {
                    CustomTextField(text: $viewModel.confirmPassword, placeHolder: "Confirm Password", secure: true)
                }
                CustomButton(iconName: getButtonIcon(loginState: viewModel.loginState), buttonText: getButtonText(loginState: viewModel.loginState)) {
                    viewModel.buttonClick()
                }
                Spacer()
                Spacer()
            }
            .padding()
            .background(Color.backgroundColor)
            .toastView(toast: $viewModel.errorToast)
        } else {
            ProgressView()
                .frame(width: 150, height: 150)
        }
    }
    
    func getButtonText(loginState: LoginState) -> String {
        return switch loginState {
        case .login: "Login"
        case .register: "Register"
        case .none: "Continue"
        }
    }
    
    func getButtonIcon(loginState: LoginState) -> String {
        return switch loginState {
        case .login: "person.fill"
        case .register: "person.fill.badge.plus"
        case .none: "arrow.right.square"
        }
    }
}

enum LoginState {
    case none, login, register
}

#Preview {
    RegisterView(viewModel: RegisterViewModel(viewHandler: ViewHandler()))
}
