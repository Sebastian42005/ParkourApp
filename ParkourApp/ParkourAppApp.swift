import SwiftUI

@main
struct DeineApp: App {
    @AppStorage("colorScheme") var selectedScheme: String = "system"

    var body: some Scene {
        WindowGroup {
            ContentView()
                .preferredColorScheme(
                    selectedScheme == "light" ? .light :
                    selectedScheme == "dark" ? .dark : nil
                )
        }
    }
}
