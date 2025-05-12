import SwiftUI

final class FancyToastWindow {
    static let shared = FancyToastWindow()

    private var window: UIWindow?

    func showToast(_ view: FancyToastView, duration: Double) {
        DispatchQueue.main.async {
            guard self.window == nil else { return }

            let hostingController = UIHostingController(
                rootView: ToastWrapper(view: view, duration: duration, dismiss: {
                    self.dismiss()
                })
            )
            hostingController.view.backgroundColor = .clear

            if let scene = UIApplication.shared.connectedScenes.first as? UIWindowScene {
                let toastWindow = UIWindow(windowScene: scene)
                toastWindow.rootViewController = hostingController
                toastWindow.windowLevel = .alert + 1
                toastWindow.backgroundColor = .clear
                toastWindow.makeKeyAndVisible()
                self.window = toastWindow
            }
        }
    }

    func dismiss() {
        self.window?.isHidden = true
        self.window = nil
    }
}

private struct ToastWrapper: View {
    let view: FancyToastView
    let duration: Double
    let dismiss: () -> Void

    @State private var isVisible = false

    var body: some View {
        VStack {
            if isVisible {
                view
                    .transition(.move(edge: .top).combined(with: .opacity))
                    .padding(.top, 40)
                    .padding(.horizontal, 16)
            }
            Spacer()
        }
        .frame(maxWidth: .infinity, maxHeight: .infinity)
        .background(Color.clear)
        .onAppear {
            withAnimation {
                isVisible = true
            }

            DispatchQueue.main.asyncAfter(deadline: .now() + duration) {
                withAnimation {
                    isVisible = false
                }

                // wait for animation to finish before removing window
                DispatchQueue.main.asyncAfter(deadline: .now() + 0.3) {
                    dismiss()
                }
            }
        }
    }
}
