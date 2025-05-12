import Combine
import SwiftUI

class RatingPickerDialogViewModel: ObservableObject {
    var cancel = Set<AnyCancellable>()
    @Published var stars: Int? = 0
    var starSize: CGFloat
    @Published var description: String = ""
    private var spotId: Int
    var onUploadComplete: (() -> Void)?
    
    init(starSize: CGFloat, spotId: Int, onUploadComplete: (() -> Void)? = nil) {
        self.starSize = starSize
        self.spotId = spotId
        self.onUploadComplete = onUploadComplete
    }
    
    func uploadRating() {
        if stars ?? 0 > 0 {
            let publisher = Service().rateSpot(spotId: spotId, stars: stars ?? 0, description: description)
            publisher.sink { error in
                switch error {
                    case .failure(let error):
                    ToastManager.shared.showToast(type: .error, title: "Rating", message: "Rating Error: \(error.localizedDescription)")
                    case .finished:
                        break
                    }
            } receiveValue: { data in
                ToastManager.shared.showToast(type: .success, title: "Rating", message: "Successfully rated spot!")
                self.onUploadComplete?()
            }.store(in: &cancel)
        }
    }
}
