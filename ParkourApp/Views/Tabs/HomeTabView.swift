import SwiftUI
import Combine

struct HomeTabView: View {
    @ObservedObject var viewModel: HomeTabViewModel
    @Environment(\.colorScheme) var colorScheme
    
    var body: some View {
        NavigationView {
           ZStack {
               Color.blue
                   .ignoresSafeArea()

               VStack {
                   TabView {
                       Tab("Map", systemImage: "map") {
                           MapTab()
                       }
                       Tab("Explore", systemImage: "magnifyingglass") {
                           ExploreTab()
                       }
                       Tab("Profile", systemImage: "person.crop.circle") {
                           ProfileTab(viewModel: ProfileTabViewModel(user: viewModel.user, colorScheme: colorScheme))
                       }
                   }
                   .accentColor(Color.primary)
               }
           }
           .navigationTitle("Spot Finder")
       }
    }
}

#Preview {
    HomeTabView(viewModel: HomeTabViewModel())
}
