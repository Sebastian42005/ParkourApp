import SwiftUI

struct ExploreTab: View {
    let columns = [
        GridItem(.flexible()),
        GridItem(.flexible())
    ]
    
    @ObservedObject var viewModel = ExploreTabViewModel()
    @Environment(\.colorScheme) var colorScheme

    var body: some View {
        NavigationStack {
            ScrollView {
                VStack {
                    CustomSearchBar(searchText: $viewModel.searchText, selectedType: $viewModel.selectedType) {
                        viewModel.performSearch()
                    }
                    if viewModel.selectedType == .spots {
                        LazyVGrid(columns: [GridItem](repeating: .init(.flexible()), count: 3)) {
                            if viewModel.isLoading {
                                ForEach(0..<6, id: \.self) { _ in
                                    RoundedRectangle(cornerRadius: 10)
                                        .fill(Color.gray.opacity(0.3))
                                        .frame(width: 100, height: 100)
                                        .redacted(reason: .placeholder)
                                }
                            } else {
                                ForEach(viewModel.spots, id: \.self) { spot in
                                    NavigationLink(destination: SpotDetailView(viewModel: SpotDetailViewModel(spotId: spot.id))) {
                                        CustomAsyncImage(
                                            lowResURL: viewModel.getLowResSpotImage(id: spot.pictures.first ?? 0),
                                            highResURL: viewModel.getSpotImage(id: spot.pictures.first ?? 0),
                                            placeholder: {
                                                SkeletonLoader()
                                                    .frame(width: viewModel.getScreenWidth(), height: viewModel.getScreenWidth())
                                            },
                                            content: { image in
                                                image
                                                    .resizable()
                                                    .scaledToFill()
                                                    .frame(width: viewModel.getScreenWidth(), height: viewModel.getScreenWidth())
                                                    .clipped()
                                                    .cornerRadius(10)
                                            }
                                        )
                                    }
                                }
                            }
                        }
                    } else {
                        UserListComponent(
                            users: viewModel.users,
                            isLoading: viewModel.isLoading,
                            onUserTapped: { username in
                                viewModel.selectedUsername = username
                            }
                        )
                    }
                }
            }
            .padding(20)
            .navigationTitle("explore_tab".localized())
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
            .sheet(isPresented: $viewModel.showFilterSheet, onDismiss: {
                viewModel.loadSpots()
            }) {
                SpotFilterSheetView(viewModel: SpotFilterSheetViewModel(viewModel: viewModel))
            }
            .navigationDestination(isPresented: Binding(
                get: { viewModel.selectedUsername != nil },
                set: { if !$0 { viewModel.selectedUsername = nil } }
            )) {
                if let username = viewModel.selectedUsername {
                    ProfileView(viewModel: ProfileViewModel(
                        username: username,
                        colorScheme: colorScheme
                    ))
                }
            }
        }
    }
}

#Preview {
    ExploreTab()
}
