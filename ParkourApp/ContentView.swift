import SwiftUI

struct ContentView: View {
    var body: some View {
        VStack {
            HStack {
                    Text("Parkour App")
                        .font(.title)
                        .padding()
                        .foregroundColor(Color.primary)
                    Spacer()
                }
            TabView {
                Tab("Map", systemImage: "map") {
                    MapTab()
                }
                Tab("Explore", systemImage: "magnifyingglass") {
                    ExploreTab()
                }
                Tab("Profile", systemImage: "person.crop.circle") {
                    ProfileTab()
                }
            }
            .accentColor(Color.primary)
        }
    }
}

#Preview {
    ContentView()
}
