import SwiftUI

struct SpotDetailView: View {
    @ObservedObject var viewModel: SpotDetailViewModel

    var body: some View {
        if !viewModel.isLoading {
            ZStack {
                ScrollView {
                    VStack(alignment: .leading, spacing: 16) {
                        
                        Text(viewModel.spot.title)
                            .font(.title2)
                            .bold()
                        
                        Text(viewModel.spot.description)
                            .font(.body)
                        
                        TabView {
                            ForEach(viewModel.spot.pictures, id: \.self) { picture in
                                CustomAsyncImage(
                                    lowResURL: viewModel.getSpotLowResImage(id: picture),
                                    highResURL: viewModel.getSpotImage(id: picture),
                                    placeholder: {
                                        SkeletonLoader()
                                            .frame(width: viewModel.getScreenWidth(), height: viewModel.getScreenWidth())
                                    },
                                    content: { image in
                                        image
                                            .resizable()
                                            .aspectRatio(contentMode: .fit)
                                            .frame(width: viewModel.getScreenWidth())
                                            .cornerRadius(10)
                                    }
                                )
                            }
                        }
                        .tabViewStyle(PageTabViewStyle())
                        .frame(height: viewModel.getScreenWidth())

                        Text("created_by".localized() + " \(viewModel.spot.user)")
                            .font(.caption)
                            .foregroundColor(.gray)
                        
                        HStack(spacing: 10) {
                            Rating(stars: viewModel.spot.rating, starSize: 25)
                                .onTapGesture {
                                    viewModel.isPopupPresented = true
                                }
                            Text(String(format: "%.1f", viewModel.spot.rating))
                                .font(.title3)
                        }
                        
                        if !viewModel.spot.attributes.isEmpty {
                            Text("attributes".localized())
                                .bold()
                            ForEach(viewModel.spot.attributes, id: \.self) { attr in
                                HStack {
                                    AsyncImage(url: viewModel.getAttributeImage(id: attr.id)) { image in
                                        image
                                            .renderingMode(.template)
                                            .resizable()
                                            .aspectRatio(contentMode: .fit)
                                            .frame(width: 20, height: 20)
                                            .foregroundColor(Color(hex: attr.color))
                                    } placeholder: {
                                        SkeletonLoader()
                                            .frame(width: 20, height: 20)
                                    }
                                    Text(attr.name)
                                    Spacer()
                                }
                            }
                        }

                        RatingList(ratings: viewModel.ratings)
                    }
                    .padding()
                }
                .navigationTitle(viewModel.spot.location.city)
                .navigationBarTitleDisplayMode(.inline)
                
                if viewModel.isPopupPresented {
                    Color.black.opacity(0.8)
                        .ignoresSafeArea()
                        .onTapGesture {
                            viewModel.isPopupPresented = false
                            viewModel.selectedStar = 0
                        }
                    VStack(spacing: 20) {
                        RatingPickerDialog(viewModel: RatingPickerDialogViewModel( starSize: 30, spotId: viewModel.spot.id, onUploadComplete: {
                            viewModel.isPopupPresented = false
                            viewModel.loadSpot(spotId: viewModel.spot.id)
                            viewModel.loadRatings(spotId: viewModel.spot.id)
                        }))
                    }
                    .frame(width: 300, height: 200)
                    .background(Color.cardColor)
                    .cornerRadius(12)
                    .shadow(color: Color.black.opacity(0.2), radius: 10, x: 0, y: 5)
                    .transition(.scale)
                }
            }
            .animation(.easeInOut, value: viewModel.isPopupPresented)
        } else {
            ScrollView {
                VStack(spacing: 16) {
                    TabView {
                        ForEach(0..<3, id: \.self) { _ in
                            SkeletonLoader()
                                .frame(width: UIScreen.main.bounds.width * 0.9, height: 250)
                                .cornerRadius(10)
                                .padding(.horizontal)
                        }
                    }
                    .tabViewStyle(PageTabViewStyle())
                    .frame(height: 250)

                    HStack(spacing: 10) {
                        Rating(starSize: 25)
                        Spacer()
                    }
                    .padding(.horizontal)

                    VStack(alignment: .leading, spacing: 8) {
                        SkeletonLoader().frame(height: 15).cornerRadius(4)
                        SkeletonLoader().frame(height: 15).cornerRadius(4)
                    }
                    .padding(.horizontal)
                    
                    RatingList()
                        .padding()
                    Spacer()
                }
                .padding(.top)
            }
        }

    }
}

#Preview {
    SpotDetailView(viewModel: SpotDetailViewModel(spotId: 1))
}
