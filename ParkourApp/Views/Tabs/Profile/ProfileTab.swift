import SwiftUI
import Combine

struct ProfileTab: View {
    @ObservedObject var viewModel: ProfileTabViewModel;
    private let fontSize = CGFloat(16)
    private let imageheight = CGFloat(30)
    private let imageWidth = CGFloat(28)
    
    var body: some View {
    VStack(spacing: 15) {
        header
        infos
            .padding(10)
            .background(Color.cardColor)
            .cornerRadius(20)
            .shadow(color: Color.black.opacity(0.2), radius: 10, x: 0, y: 5)
        spots
        Spacer()
    }
    .padding(20)
    }
    
    var infos: some View {
        HStack(spacing: 20) {
            Spacer()
            VStack(alignment: .center, spacing: 5) {
                
                Image(systemName: "mappin.and.ellipse")
                    .resizable()
                    .frame(width: imageWidth - 2, height: imageheight)
                    .foregroundColor(Color.primary)
                
                Text(String(viewModel.user.spotsAmount))
                    .fontWeight(.bold)
                    .font(.system(size: fontSize))
                
                Text("Spots")
                    .font(.system(size: fontSize))
            }
            Divider()
                .frame(height: 70)
            VStack(alignment: .center, spacing: 5) {
                Image(systemName: "person.fill")
                    .resizable()
                    .frame(width: imageWidth, height: imageheight)
                    .foregroundColor(Color.primary)
                
                Text(String(viewModel.user.follower))
                    .fontWeight(.bold)
                    .font(.system(size: fontSize))
                
                Text("Followers")
                    .font(.system(size: fontSize))
            }
            Divider()
                .frame(height: 70)
            VStack(alignment: .center, spacing: 5) {
                Image(systemName: "person.fill")
                    .resizable()
                    .frame(width: imageWidth, height: imageheight)
                    .foregroundColor(Color.primary)
                
                Text(String(viewModel.user.follows))
                    .fontWeight(.bold)
                    .font(.system(size: fontSize))
                
                Text("Following")
                    .font(.system(size: fontSize))
            }
            Spacer()
        }
    }
    
    var header: some View {
        HStack(spacing: 25) {
            AsyncImage(url: viewModel.getProfilePicture()) { image in
                image
                    .resizable()
                    .scaledToFill()
                    .frame(width: 70, height: 70)
                    .clipped()
                    .cornerRadius(100)
            } placeholder: {
                ProgressView()
                    .frame(width: 150, height: 150)
            }
            VStack(alignment: .leading, spacing: 5) {
                Text(viewModel.user.username)
                    .font(.title2)
                    .frame(alignment: .leading)
                Text(viewModel.user.description)
            }
            Spacer()
        }
    }
    
    var spots: some View {
        VStack {
            ScrollView {
                LazyVGrid(columns: [GridItem].init(repeating: GridItem(.flexible()), count: 3)) {
                    ForEach(viewModel.spots, id: \.self) { spot in
                        NavigationLink(destination: SpotDetailView(viewModel: SpotDetailViewModel(spotId: spot.id))) {
                            AsyncImage(url: viewModel.getSpotImage(id: spot.pictures.first!)) { image in
                                image
                                    .resizable()
                                    .scaledToFill()
                                    .frame(width: viewModel.getScreenWidth(), height: viewModel.getScreenWidth())
                                    .clipped()
                                    .cornerRadius(10)
                            } placeholder: {
                                ProgressView()
                                    .frame(width: 150, height: 150)
                            }
                        }
                    }
                    
                }
            }
        }
    }
}

#Preview {
    ProfileTab(viewModel: ProfileTabViewModel(user: User(username: "Sebastian", description: "Hier ist eine Test Beschreibung", spotsAmount: 4, follower: 10, follows: 5), colorScheme: .dark))
}
