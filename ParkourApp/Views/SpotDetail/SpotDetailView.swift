import SwiftUI

struct SpotDetailView: View {
    @ObservedObject var viewModel: SpotDetailViewModel
    @State private var isPopupPresented = false 

    var body: some View {
        if !viewModel.isLoading {
            ZStack {
                VStack {
                    AsyncImage(url: viewModel.getSpotImage(id: viewModel.spot.pictures.first!)) { image in
                        image
                            .resizable()
                            .aspectRatio(contentMode: .fit)
                            .frame(width: viewModel.getScreenWidth())
                            .clipped()
                            .cornerRadius(10)
                    } placeholder: {
                        ProgressView()
                            .frame(width: 150, height: 150)
                    }
                    
                    HStack {
                        Text(viewModel.spot.description)
                        Spacer()
                    }
                    
                    HStack(alignment: .center, spacing: 10) {
                        Rating(stars: viewModel.spot.rating, starSize: 25)
                            .onTapGesture {
                                isPopupPresented = true
                            }
                        Text(String(format: "%.1f", viewModel.spot.rating))
                            .font(.title3)
                        Spacer()
                    }
                    
                    Spacer()
                }
                .padding()
                .navigationTitle(viewModel.spot.location.city)
                .navigationBarTitleDisplayMode(.inline)
                
                if isPopupPresented {
                    Color.black.opacity(0.8)
                        .ignoresSafeArea()
                        .onTapGesture {
                            isPopupPresented = false
                            viewModel.selectedStar = 0
                        }
                    
                    VStack(spacing: 20) {
                        RatingPicker(stars: $viewModel.selectedStar, starSize: 30)
                    }
                    .frame(width: 300, height: 200)
                    .background(Color.cardColor)
                    .cornerRadius(12)
                    .shadow(color: Color.black.opacity(0.2), radius: 10, x: 0, y: 5)
                    .transition(.scale)
                }
            }
            .animation(.easeInOut, value: isPopupPresented)
        } else {
            ProgressView()
                .frame(width: 150, height: 150)
        }
    }
}

#Preview {
    SpotDetailView(viewModel: SpotDetailViewModel(spotId: 1))
}
