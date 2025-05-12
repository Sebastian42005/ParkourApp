import SwiftUI

struct RegisterView: View {
    @ObservedObject var viewModel: RegisterViewModel
    
    var body: some View {
        if !viewModel.isLoading {
            VStack(spacing: 15) {
                Spacer()
                
                // Register title
                Text(viewModel.loginState == .register
                     ? (viewModel.registerStep == 1 ? "register".localized() : "choose_your_favorite_sports".localized())
                     : "login".localized())
                    .font(.title)
                    .fontWeight(.bold)
                    .foregroundColor(Color.primary)
                    .padding(.bottom, 20)
                    .transition(.opacity.combined(with: .slide))
                
                // Sports selection
                if viewModel.loginState == .register && viewModel.registerStep == 2 {
                    Text("choose_up_to_5_sports".localized())
                        .font(.subheadline)
                        .foregroundColor(.gray)
                    TextField("search_sports".localized(), text: $viewModel.searchSportText)
                        .padding(10)
                        .background(Color.gray.opacity(0.15))
                        .cornerRadius(10)
                        .padding(.horizontal)
                    
                    WrapView(data: viewModel.filteredSports, spacing: 10) { sport in
                        let isSelected = viewModel.selectedSports.contains(sport)
                        HStack {
                            Image(systemName: sport.symbol)
                                .font(.system(size: 20))
                            Text(sport.name)
                        }
                        .padding(.horizontal, 12)
                        .padding(.vertical, 8)
                        .background(isSelected ? Color.primary : Color.gray.opacity(0.7))
                        .foregroundColor(isSelected ? .white : .black)
                        .cornerRadius(16)
                        .onTapGesture {
                            if isSelected {
                                viewModel.selectedSports.removeAll { $0 == sport }
                            } else if viewModel.selectedSports.count < 5 {
                                viewModel.selectedSports.append(sport)
                            }
                        }
                    }
                    .padding()
                } else {
                    CustomTextField(text: $viewModel.username, placeHolder: "username".localized())
                    if viewModel.loginState == .register {
                        CustomTextField(text: $viewModel.email, placeHolder: "email".localized())
                            .transition(.move(edge: .top).combined(with: .opacity))
                    }
                    CustomTextField(text: $viewModel.password, placeHolder: "password".localized(), secure: true)
                    if viewModel.loginState == .register {
                        CustomTextField(text: $viewModel.confirmPassword, placeHolder: "confirm_password".localized(), secure: true)
                            .transition(.move(edge: .top).combined(with: .opacity))
                    }
                }

                VStack {
                    CustomButton(
                        iconName: getButtonIcon(loginState: viewModel.loginState),
                        buttonText: viewModel.loginState == .register
                            ? (viewModel.registerStep == 1 ? "next".localized() : "register".localized())
                            : "login".localized(),
                        backgroundColor: viewModel.isFormValid ? Color.primary : Color.gray
                    ) {
                        if !viewModel.isFormValid {
                            return
                        }
                        if viewModel.registerStep == 2 && viewModel.selectedSports.isEmpty {
                            ToastManager.shared.showToast(
                                type: .error,
                                title: "register".localized(),
                                message: "please_select_at_least_one_sport".localized()
                            )
                            return
                        }
                        viewModel.buttonClick()
                    }
                    .disabled(!viewModel.isFormValid)

                    Button(action: {
                        withAnimation(.easeInOut) {
                            viewModel.loginState = viewModel.loginState == .login ? .register : .login
                            viewModel.registerStep = 1
                        }
                    }) {
                        Text(viewModel.loginState == .login
                             ? "dont_have_an_account".localized()
                             : "already_have_an_account".localized())
                            .font(.footnote)
                            .foregroundColor(.blue)
                            .padding(.top, 4)
                    }
                }

                Spacer()
                Spacer()
            }
            .padding()
            .background(Color.backgroundColor)
            .animation(.easeInOut, value: viewModel.loginState)
        } else {
            ProgressView()
                .frame(width: 150, height: 150)
        }
    }

    func getButtonIcon(loginState: LoginState) -> String {
        loginState == .login ? "person.fill" : "person.fill.badge.plus"
    }
}

struct WrapView<Data: RandomAccessCollection, Content: View>: View where Data.Element: Hashable {
    let data: Data
    let spacing: CGFloat
    let content: (Data.Element) -> Content

    init(data: Data, spacing: CGFloat = 8, @ViewBuilder content: @escaping (Data.Element) -> Content) {
        self.data = data
        self.spacing = spacing
        self.content = content
    }

    var body: some View {
        var width: CGFloat = 0
        var height: CGFloat = 0

        return GeometryReader { geometry in
            ZStack(alignment: .topLeading) {
                ForEach(data, id: \.self) { item in
                    content(item)
                        .padding([.horizontal, .vertical], 4)
                        .alignmentGuide(.leading, computeValue: { d in
                            if abs(width - d.width) > geometry.size.width {
                                width = 0
                                height -= d.height + spacing
                            }
                            let result = width
                            if item == data.last {
                                width = 0 // reset for next render
                            } else {
                                width -= d.width + spacing
                            }
                            return result
                        })
                        .alignmentGuide(.top) { _ in
                            let result = height
                            if item == data.last {
                                height = 0
                            }
                            return result
                        }
                }
            }
        }.frame(maxHeight: .infinity, alignment: .top)
    }
}

enum LoginState {
    case login, register
}

#Preview {
    RegisterView(viewModel: RegisterViewModel(viewHandler: ViewHandler()))
}
