import Foundation

struct User: Codable, Hashable {
    let username: String
    let description: String
    let spotsAmount: Int32
    let follower: Int32
    let follows: Int32
    
    init(username: String, description: String, spotsAmount: Int32, follower: Int32, follows: Int32) {
        self.username = username
        self.description = description
        self.spotsAmount = spotsAmount
        self.follower = follower
        self.follows = follows
    }
}
