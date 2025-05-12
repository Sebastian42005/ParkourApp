import Foundation

extension String {
    func localized() -> String {
        let localizedString = NSLocalizedString(self, comment: "")
        if localizedString == self {
            print("⚠️ Missing localization for key: '\(self)'")
         }
        return localizedString
    }
}
