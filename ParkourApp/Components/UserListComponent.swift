import SwiftUI

struct UserListComponent: View {
    @Environment(\.colorScheme) var colorScheme
    var users: [String]
    var isLoading: Bool
    var onUserTapped: (String) -> Void

    var body: some View {
        ScrollView {
            VStack(spacing: 5) {
                if !isLoading {
                    ForEach(users, id: \.self) { username in
                        Button {
                            onUserTapped(username)
                        } label: {
                            HStack(spacing: 4) {
                                AsyncImage(url: getProfileImage(username: username)) { image in
                                    image
                                        .resizable()
                                        .scaledToFill()
                                        .frame(width: 40, height: 40)
                                        .clipped()
                                        .cornerRadius(100)
                                } placeholder: {
                                    SkeletonLoader()
                                        .frame(width: 40, height: 40)
                                        .clipped()
                                        .cornerRadius(100)
                                }
                                Text(username)
                                    .font(.subheadline)
                                Spacer()
                            }
                        }
                        Divider()
                    }
                } else {
                    ForEach(0..<10) { _ in
                        HStack(spacing: 4) {
                            SkeletonLoader()
                                .frame(width: 40, height: 40)
                                .clipped()
                                .cornerRadius(100)
                            SkeletonLoader()
                                .frame(width: .infinity / 2, height: 30)
                            Spacer()
                        }
                        Divider()
                    }
                }
            }.padding()
        }
    }

    func getProfileImage(username: String) -> URL {
        return Service().getProfilePicture(username: username, colorScheme: colorScheme)
    }
}


#Preview {
    UserListComponent(
        users: ["Sebastian", "Emil", "Marcus", "Louis"],
        isLoading: false,
        onUserTapped: { _ in }
    )
}
