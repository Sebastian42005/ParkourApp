import SwiftUI
import Combine

class GlobalViewModel: ObservableObject {
    @Published var currentView: CurrentViewEnum = .home
    
}

enum CurrentViewEnum: String, CaseIterable {
    case home
    case uploadImage
}

