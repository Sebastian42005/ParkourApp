import Foundation

struct FeedbackResponse: Codable, Hashable {
    let message: String
    let type: String
    let username: String
    
    init(message: String, type: String, username: String) {
        self.message = message
        self.type = type
        self.username = username
    }
}
