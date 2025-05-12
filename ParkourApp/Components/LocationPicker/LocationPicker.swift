import SwiftUI
import MapKit

struct LocationPicker: View {
    @Binding var selectedLocation: CLLocationCoordinate2D?
    @Binding var isPresented: Bool
    var title: String
    var sport: Sport?

    var body: some View {
        ZStack {
            MapReader { reader in
                Map {
                    if let selectedLocation {
                        if let sport {
                            Marker(title, systemImage: sport.symbol, coordinate: selectedLocation)
                                .tint(sport.getColor())
                        } else {
                            Marker(title, systemImage: "", coordinate: selectedLocation)
                        }
                    }
                }
                .onTapGesture { screenCoord in
                    if let pinLocation = reader.convert(screenCoord, from: .local),
                       CLLocationCoordinate2DIsValid(pinLocation),
                       pinLocation.latitude.isFinite,
                       pinLocation.longitude.isFinite {
                        
                        self.selectedLocation = pinLocation
                        
                    } else {
                        print("Ungültige Koordinate: \(String(describing: reader.convert(screenCoord, from: .local)))")
                    }
                }
            }

            VStack {
                Spacer()
                CustomButton(iconName: "checkmark.circle", buttonText: "pick_location".localized()) {
                    isPresented = false
                }
            }
            .padding()
        }
    }
}
