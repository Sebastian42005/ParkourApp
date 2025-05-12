import SwiftUI

final class ImageCache {
    static let shared = ImageCache()

    private init() {}

    private let cache = NSCache<NSURL, UIImage>()

    func image(for url: URL) -> UIImage? {
        return cache.object(forKey: url as NSURL)
    }

    func insertImage(_ image: UIImage?, for url: URL) {
        guard let image = image else { return }
        cache.setObject(image, forKey: url as NSURL)
    }
}
