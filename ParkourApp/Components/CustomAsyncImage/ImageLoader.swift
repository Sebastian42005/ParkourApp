import SwiftUI
import Combine

class ImageLoader: ObservableObject {
    @Published var image: UIImage?
    @Published var isHighResLoaded: Bool = false

    private var lowResURL: URL?
    private var highResURL: URL?
    private var cancellableLowRes: AnyCancellable?
    private var cancellableHighRes: AnyCancellable?

    init(lowResURL: URL?, highResURL: URL?) {
        self.lowResURL = lowResURL
        self.highResURL = highResURL
    }

    func load() {
        guard !isHighResLoaded else { return }

        // Check cache for high-res image first
        if let highResURL = highResURL, let cachedHighRes = ImageCache.shared.image(for: highResURL) {
            self.image = cachedHighRes
            self.isHighResLoaded = true
            return
        }

        // Check cache for low-res image
        if image == nil, let lowResURL = lowResURL, let cachedLowRes = ImageCache.shared.image(for: lowResURL) {
            self.image = cachedLowRes
        } else if let lowResURL = lowResURL {
            cancellableLowRes = URLSession.shared.dataTaskPublisher(for: lowResURL)
                .map { data -> UIImage? in
                    let image = UIImage(data: data.data)
                    if let image = image {
                        ImageCache.shared.insertImage(image, for: lowResURL)
                    }
                    return image
                }
                .replaceError(with: nil)
                .receive(on: DispatchQueue.main)
                .sink { [weak self] in self?.image = $0 }
        }

        // Load high-res image in background
        if let highResURL = highResURL {
            cancellableHighRes = URLSession.shared.dataTaskPublisher(for: highResURL)
                .map { data -> UIImage? in
                    let image = UIImage(data: data.data)
                    if let image = image {
                        ImageCache.shared.insertImage(image, for: highResURL)
                    }
                    return image
                }
                .replaceError(with: nil)
                .receive(on: DispatchQueue.main)
                .sink { [weak self] in
                    if let image = $0 {
                        self?.image = image
                        self?.isHighResLoaded = true
                    }
                }
        }
    }

}
