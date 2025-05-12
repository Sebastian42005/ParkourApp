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
                       Tab("map_tab".localized(), systemImage: "map") {
                           MapTab(viewModel: MapTabViewModel())
                       }
                       Tab("explore_tab".localized(), systemImage: "magnifyingglass") {
                           ExploreTab()
                       }
                       Tab("profile_tab".localized(), systemImage: "person.crop.circle") {
                           ProfileView(viewModel: ProfileViewModel(username: viewModel.user.username, colorScheme: colorScheme))
                       }
                   }
                   .accentColor(Color.primary)
                   .navigationBarHidden(true)
               }
           }
       }
    }
}

#Preview {
    HomeTabView(viewModel: HomeTabViewModel(viewHandler: ViewHandler()))
}
