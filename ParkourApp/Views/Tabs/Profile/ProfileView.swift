import SwiftUI
import Combine

struct ScrollOffsetPreferenceKey: PreferenceKey {
    static var defaultValue: CGFloat = 0
    static func reduce(value: inout CGFloat, nextValue: () -> CGFloat) {
        value = nextValue()
    }
}

struct ProfileView: View {
    @ObservedObject var viewModel: ProfileViewModel
    private let fontSize = CGFloat(16)
    private let imageHeight = CGFloat(30)
    private let imageWidth = CGFloat(28)

    var body: some View {
        NavigationStack {
            if viewModel.isLoading {
                loadingView
            } else {
                ScrollView {
                    VStack(spacing: 15) {
                        GeometryReader { geo in
                            Color.clear
                                .preference(key: ScrollOffsetPreferenceKey.self, value: geo.frame(in: .named("scroll")).minY)
                        }
                        .frame(height: 0)
                        
                        header
                        infos
                            .padding(10)
                            .background(Color.cardColor)
                            .cornerRadius(20)
                            .shadow(color: Color.black.opacity(0.2), radius: 10, x: 0, y: 5)
                        spotsContent
                        Spacer()
                    }
                    .padding(20)
                }
                .coordinateSpace(name: "scroll")
                .onPreferenceChange(ScrollOffsetPreferenceKey.self) { value in
                    withAnimation {
                        viewModel.showUsernameAsTitle = value < -80
                    }
                }
                .navigationTitle(viewModel.showUsernameAsTitle ? viewModel.user!.username : "profile".localized())
                .navigationBarTitleDisplayMode(.inline)
                .navigationDestination(isPresented: $viewModel.showCreateSpotSheet, destination: {
                    UploadSpotView(viewModel: UploadSpotViewModel(showView: $viewModel.showCreateSpotSheet))
                })
                .toolbar {
                    ToolbarItem(placement: .navigationBarTrailing) {
                        Button(action: {
                            viewModel.showSettings = true
                        }) {
                            Image(systemName: "gearshape")
                                .font(.system(size: 20))
                                .foregroundColor(.gray)
                        }
                    }
                }
                .sheet(isPresented: $viewModel.showSettings) {
                    SettingsView()
                }
                .overlay(
                    fabButton,
                    alignment: .bottomTrailing
                )
            }
        }
    }
    
    var loadingView: some View {
        VStack(spacing: 15) {
            HStack(spacing: 25) {
                SkeletonLoader()
                    .frame(width: 70, height: 70)
                    .clipped()
                    .cornerRadius(100)

                VStack(alignment: .leading, spacing: 5) {
                    SkeletonLoader()
                        .frame(width: 120, height: 20)
                        .cornerRadius(5)
                    SkeletonLoader()
                        .frame(width: 200, height: 15)
                        .cornerRadius(5)
                }
            }

            // Infos Skeleton
            HStack(spacing: 20) {
                Spacer()
                statSkeletonBlock()
                Divider().frame(height: 70)
                statSkeletonBlock()
                Divider().frame(height: 70)
                statSkeletonBlock()
                Spacer()
            }
            .padding(10)
            .background(Color.cardColor)
            .cornerRadius(20)
            .shadow(color: Color.black.opacity(0.2), radius: 10, x: 0, y: 5)
            HStack {
                ForEach(0..<3, id: \.self) { _ in
                    SkeletonLoader()
                        .frame(width: viewModel.getScreenWidth(), height: viewModel.getScreenWidth())
                }
            }
            HStack {
                ForEach(0..<3, id: \.self) { _ in
                    SkeletonLoader()
                        .frame(width: viewModel.getScreenWidth(), height: viewModel.getScreenWidth())
                }
            }

            Spacer()
        }
        .padding(20)
    }

    func statSkeletonBlock() -> some View {
        VStack(alignment: .center, spacing: 5) {
            SkeletonLoader()
                .frame(width: imageWidth, height: imageHeight)
                .cornerRadius(5)
            SkeletonLoader()
                .frame(width: 30, height: 15)
                .cornerRadius(5)
            SkeletonLoader()
                .frame(width: 50, height: 15)
                .cornerRadius(5)
        }
    }


    var header: some View {
        HStack(alignment: .top, spacing: 20) {
            AsyncImage(url: viewModel.getProfilePicture()) { image in
                image
                    .resizable()
                    .scaledToFill()
                    .frame(width: 70, height: 70)
                    .clipped()
                    .cornerRadius(100)
            } placeholder: {
                SkeletonLoader()
                    .frame(width: 70, height: 70)
                    .clipped()
                    .cornerRadius(100)
            }
            VStack(alignment: .leading, spacing: 5) {
                Text(viewModel.user!.username)
                    .font(.title2)
                    .frame(alignment: .leading)
                Text(viewModel.user!.description)
                    .font(.caption)
                    .frame(alignment: .leading)
            }
        }
        .frame(maxWidth: .infinity, alignment: .leading)
    }

    var infos: some View {
        HStack(spacing: 20) {
            Spacer()
            statBlock(icon: "mappin.and.ellipse", value: viewModel.user!.spotsAmount, label: "spots".localized())
            Divider().frame(height: 70)
            statBlock(icon: "person.fill", value: viewModel.user!.follower, label: "followers".localized())
            Divider().frame(height: 70)
            statBlock(icon: "person.fill", value: viewModel.user!.follows, label: "following".localized())
            Spacer()
        }
    }

    func statBlock(icon: String, value: Int, label: String) -> some View {
        VStack(alignment: .center, spacing: 5) {
            Image(systemName: icon)
                .resizable()
                .frame(width: imageWidth, height: imageHeight)
                .foregroundColor(Color.primary)
            Text("\(value)")
                .fontWeight(.bold)
                .font(.system(size: fontSize))
            Text(label)
                .font(.system(size: fontSize))
        }
    }

    var spotsContent: some View {
        LazyVGrid(columns: [GridItem](repeating: GridItem(.flexible()), count: 3)) {
            ForEach(viewModel.spots, id: \ .self) { spot in
                NavigationLink(destination: SpotDetailView(viewModel: SpotDetailViewModel(spotId: spot.id))) {
                    CustomAsyncImage(
                        lowResURL: viewModel.getSpotLowResImage(id: spot.pictures.first ?? 0),
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

    var fabButton: some View {
        Group {
            if viewModel.isOwnProfile {
                Button(action: {
                    viewModel.showCreateSpotSheet.toggle()
                }) {
                    Image(systemName: "plus")
                        .font(.system(size: 24))
                        .foregroundColor(.white)
                        .frame(width: 56, height: 56)
                        .background(Color.blue)
                        .clipShape(Circle())
                        .shadow(color: Color.black.opacity(0.3), radius: 5, x: 0, y: 3)
                }
                .padding(20)
            }
        }
    }
}

#Preview {
    ProfileView(viewModel: ProfileViewModel(username: "Sebastian", colorScheme: .dark))
}
