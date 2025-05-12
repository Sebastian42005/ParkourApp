import Foundation

struct User: Codable, Hashable {
    let username: String
    let description: String
    let spotsAmount: Int
    let follower: Int
    let follows: Int
    let isFollowed: Bool
    let averageRating: Double
    
    init(username: String, description: String, spotsAmount: Int, follower: Int, follows: Int, isFollowed: Bool, averageRating: Double) {
        self.username = username
        self.description = description
        self.spotsAmount = spotsAmount
        self.follower = follower
        self.follows = follows
        self.isFollowed = isFollowed
        self.averageRating = averageRating
    }
}
