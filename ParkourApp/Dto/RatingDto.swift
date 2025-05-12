import Foundation

struct RatingDto: Codable, Hashable {
    let id: Int
    let stars: Double
    let message: String
    let username: String
    
    init(id: Int, stars: Double, message: String, username: String) {
        self.stars = stars
        self.message = message
        self.id = id
        self.username = username
    }
}
