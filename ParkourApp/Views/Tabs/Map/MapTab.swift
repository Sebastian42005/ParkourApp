import SwiftUI
import MapKit

struct MapTab: View {
    @StateObject var viewModel: MapTabViewModel

    var body: some View {
        NavigationStack {
            ZStack {
                Map(position: $viewModel.cameraPosition, selection: $viewModel.selectedItemId) {
                    ForEach(viewModel.spots, id: \.self) { spot in
                        Marker(
                            spot.title,
                            systemImage: spot.sport.symbol,
                            coordinate: CLLocationCoordinate2D(
                                latitude: spot.latitude,
                                longitude: spot.longitude
                            )
                        )
                        .tint(spot.sport.getColor())
                        .tag(spot.id)
                    }
                }
                .mapStyle(.standard)
                .mapControls {
                    MapUserLocationButton()
                }
            }
            .navigationTitle("spots_nearby".localized())  // Lokalisierter Titel
            .navigationBarTitleDisplayMode(.inline)
            .toolbar {
                ToolbarItem(placement: .navigationBarTrailing) {
                    Button {
                        viewModel.showFilterSheet = true
                    } label: {
                        Image(systemName: viewModel.isFilterActive ? "line.3.horizontal.decrease.circle.fill" : "line.3.horizontal.decrease.circle")
                    }
                }
            }
            .sheet(isPresented: $viewModel.showInfoSheet, onDismiss: {
                viewModel.resetSelection()
            }) {
                MarkerInfoSheetView(
                    spot: viewModel.selectedSpot,
                    currentDetent: $viewModel.currentDetent,
                    showDetails: $viewModel.showSpotDetails,
                    showSheet: $viewModel.showInfoSheet
                )
                .presentationDetents([.fraction(0.4), .large], selection: $viewModel.currentDetent)
                .presentationDragIndicator(.hidden)
            }
            .sheet(isPresented: $viewModel.showFilterSheet, onDismiss: {
                viewModel.loadMarkers()
            }) {
                SpotFilterSheetView(viewModel: SpotFilterSheetViewModel(viewModel: viewModel))
            }

            // Navigation to Spot Detail
            if let spot = viewModel.selectedSpot {
                NavigationLink(
                    destination: SpotDetailView(viewModel: SpotDetailViewModel(spotId: spot.id)),
                    isActive: $viewModel.showSpotDetails
                ) {
                    EmptyView()
                }
            }
        }
    }
}

#Preview {
    MapTab(viewModel: MapTabViewModel())
}
