import SwiftUI

struct SettingsView: View {
    @AppStorage("colorScheme") private var selectedScheme: String = "system"
    @State private var showFeedbackForm = false

    var body: some View {
        NavigationStack {
            Form {
                Section(header: Text("appearance".localized())) {
                    Picker("mode".localized(), selection: $selectedScheme) {
                        Text("system".localized()).tag("system")
                        Text("light".localized()).tag("light")
                        Text("dark".localized()).tag("dark")
                    }
                    .pickerStyle(SegmentedPickerStyle())
                }

                Section(header: Text("feedback_support".localized())) {
                    Button {
                        showFeedbackForm = true
                    } label: {
                        Label("give_feedback".localized(), systemImage: "bubble.left.and.bubble.right")
                    }

                    Link(destination: URL(string: "mailto:support@deineapp.de")!) {
                        Label("contact_support".localized(), systemImage: "envelope")
                    }
                }

                Section(header: Text("general".localized())) {
                    NavigationLink(destination: Text("App-Informationen...")) {
                        Label("about_app".localized(), systemImage: "info.circle")
                    }

                    NavigationLink(destination: Text("Datenschutzrichtlinie...")) {
                        Label("privacy_policy".localized(), systemImage: "lock.shield")
                    }

                    NavigationLink(destination: LanguageSettingsView()) {
                        Label("language".localized(), systemImage: "globe")
                    }

                    Button(action: {
                        // App Bewertungs-Button, ggf. mit URL öffnen
                    }) {
                        Label("rate_app".localized(), systemImage: "star")
                    }
                }

                Section {
                    HStack {
                        Spacer()
                        Text("version".localized() + " 1.0.0")
                            .foregroundColor(.secondary)
                        Spacer()
                    }
                }
            }
            .navigationTitle("settings".localized())
            .sheet(isPresented: $showFeedbackForm) {
                FeedbackView()
            }
        }
    }
}

#Preview {
    SettingsView()
}
