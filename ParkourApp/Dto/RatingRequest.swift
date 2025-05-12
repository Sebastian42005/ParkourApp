import Foundation

struct RatingRequest: Codable, Hashable {
    let stars: Int
    let message: String
    
    init(stars: Int, message: String) {
        self.stars = stars
        self.message = message
    }
}
