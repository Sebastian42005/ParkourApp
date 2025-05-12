import Foundation

struct Feedback: Codable, Hashable {
    let id: Int;
    let message: String;
    let type: FeedbackType;
    let user: User
}

enum FeedbackType: String, Codable, CaseIterable, Identifiable {
    case BUG
    case FEATURE_REQUEST
    case IMPROVEMENT
    case PRAISE
    case QUESTION
    case OTHER

    var id: String { self.rawValue }

    var label: String {
        switch self {
        case .BUG: return NSLocalizedString("bug", comment: "Feedback type: Bug")
        case .FEATURE_REQUEST: return NSLocalizedString("feature_request", comment: "Feedback type: Feature Request")
        case .IMPROVEMENT: return NSLocalizedString("improvement", comment: "Feedback type: Improvement")
        case .PRAISE: return NSLocalizedString("praise", comment: "Feedback type: Praise")
        case .QUESTION: return NSLocalizedString("question", comment: "Feedback type: Question")
        case .OTHER: return NSLocalizedString("other", comment: "Feedback type: Other")
        }
    }
}
