import Foundation

struct FeedbackRequest: Codable, Hashable {
    let message: String
    let type: String
    
    init(message: String, type: String) {
        self.message = message
        self.type = type
    }
}
