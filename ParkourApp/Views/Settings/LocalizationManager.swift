import Foundation

class LocalizationManager {
    static let shared = LocalizationManager()
    
    func setLanguage(_ languageCode: String) {
        UserDefaults.standard.set([languageCode], forKey: "AppleLanguages")
        UserDefaults.standard.synchronize()
        ToastManager.shared.showToast(type: .warning, title: "restart_app".localized(), message: "restart_app_message".localized())

    }
}
