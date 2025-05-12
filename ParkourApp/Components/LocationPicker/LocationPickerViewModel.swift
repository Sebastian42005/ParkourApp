import SwiftUI
import MapKit

class LocationPickerViewModel: ObservableObject {
    @Binding var selectedLocation: CLLocationCoordinate2D?
    @Binding var showLocationPicker: Bool
    @Published var sport: Sport?
    @Published var title: String
    
    init(selectedLocation: Binding<CLLocationCoordinate2D?>, showLocationPicker: Binding<Bool>, sport: Sport? = nil, title: String? = nil) {
        self._selectedLocation = selectedLocation
        self._showLocationPicker = showLocationPicker
        self.sport = sport
        self.title = title ?? ""
    }
}
