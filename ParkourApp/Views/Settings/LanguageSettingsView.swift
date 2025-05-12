import SwiftUI

struct LanguageSettingsView: View {
    @AppStorage("selectedLanguage") private var selectedLanguage: String = Locale.current.language.languageCode?.identifier ?? "en"
    
    var body: some View {
        Form {
            Section(header: Text("language".localized())) {
                Picker("language".localized(), selection: $selectedLanguage) {
                    Text("Deutsch").tag("de")
                    Text("English").tag("en")
                }
                .pickerStyle(.inline)
            }
        }
        .navigationTitle("language".localized())
        .onChange(of: selectedLanguage) { newLang in
            LocalizationManager.shared.setLanguage(newLang)
        }
    }
}
