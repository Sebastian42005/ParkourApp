import SwiftUI
import Combine

class FeedbackViewModel: ObservableObject {
    var cancel = Set<AnyCancellable>()
    @Published var selectedFeedbackType: FeedbackType?
    @Published var feedbackText = ""
    @Published var isLoading: Bool = false
    @Published var didUploadFeedback: Bool = false
    
    func uploadFeedback() {
        guard let type = selectedFeedbackType else { return }

        isLoading = true
        let request = FeedbackRequest(message: feedbackText, type: type.rawValue)

        Service().uploadFeedback(feedback: request)
            .sink { completion in
                switch completion {
                case .failure(let error):
                    self.isLoading = false
                    print("Fehler beim Senden: \(error)")
                    ToastManager.shared.showToast(
                        type: .error,
                        title: NSLocalizedString("error", comment: ""),
                        message: NSLocalizedString("feedback_send_failed", comment: "")
                    )
                case .finished:
                    self.isLoading = false
                }
            } receiveValue: { _ in
                ToastManager.shared.showToast(
                    type: .success,
                    title: NSLocalizedString("thank_you", comment: ""),
                    message: NSLocalizedString("feedback_sent_success", comment: "")
                )
                DispatchQueue.main.async {
                    self.didUploadFeedback = true
                }
            }
            .store(in: &cancel)
    }
}
