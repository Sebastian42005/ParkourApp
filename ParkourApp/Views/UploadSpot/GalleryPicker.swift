import SwiftUI
import PhotosUI

struct GalleryPicker: UIViewControllerRepresentable {
    var didFinishPicking: ([UIImage]) -> Void
    @Binding var isImagePickerPresented: Bool

    func makeUIViewController(context: Context) -> PHPickerViewController {
        var configuration = PHPickerConfiguration()
        configuration.filter = .images
        configuration.selectionLimit = 0 // 0 = unbegrenzt viele Bilder wählbar

        let picker = PHPickerViewController(configuration: configuration)
        picker.delegate = context.coordinator
        return picker
    }

    func updateUIViewController(_ uiViewController: PHPickerViewController, context: Context) {}

    func makeCoordinator() -> Coordinator {
        Coordinator(parent: self)
    }

    class Coordinator: NSObject, PHPickerViewControllerDelegate {
        var parent: GalleryPicker

        init(parent: GalleryPicker) {
            self.parent = parent
        }

        func picker(_ picker: PHPickerViewController, didFinishPicking results: [PHPickerResult]) {
            parent.isImagePickerPresented = false

            let itemProviders = results.map { $0.itemProvider }
            var images: [UIImage] = []

            let dispatchGroup = DispatchGroup()

            for provider in itemProviders {
                if provider.canLoadObject(ofClass: UIImage.self) {
                    dispatchGroup.enter()
                    provider.loadObject(ofClass: UIImage.self) { image, error in
                        if let uiImage = image as? UIImage {
                            images.append(uiImage)
                        }
                        dispatchGroup.leave()
                    }
                }
            }

            dispatchGroup.notify(queue: .main) {
                self.parent.didFinishPicking(images)
            }
        }
    }
}
