import SwiftUI

struct ContentView: View {
    @ObservedObject var viewHandler = ViewHandler()
    
    var body: some View {
        switch (viewHandler.page) {
        case .empty : EmptyView()
        case .login : RegisterView(viewModel: RegisterViewModel(viewHandler: viewHandler))
        case .tabview : HomeTabView(viewModel: HomeTabViewModel(viewHandler: viewHandler))
        }
    }
}

#Preview {
    ContentView()
}
