import SwiftUI

class ToastManager: ObservableObject {
    static let shared = ToastManager()
    
    @Published var currentToast: FancyToast? = nil
    @Published var isVisible: Bool = false
    
    func showToast(type: FancyToastStyle, title: String, message: String, duration: Double = 3) {
        DispatchQueue.main.async {
            let toastView = FancyToastView(
                type: type,
                title: title,
                message: message,
                onCancelTapped: {
                    FancyToastWindow.shared.dismiss()
                }
            )
            
            FancyToastWindow.shared.showToast(toastView, duration: duration)
        }
    }
}
