import SwiftUI

struct CustomAsyncImage<Content: View, Placeholder: View>: View {
    @StateObject private var loader: ImageLoader
    private let placeholder: Placeholder
    private let content: (Image) -> Content

    @State private var blurRadius: CGFloat = 10
    @State private var showHighRes = false

    init(
        lowResURL: URL?,
        highResURL: URL?,
        @ViewBuilder placeholder: () -> Placeholder,
        @ViewBuilder content: @escaping (Image) -> Content
    ) {
        _loader = StateObject(wrappedValue: ImageLoader(lowResURL: lowResURL, highResURL: highResURL))
        self.placeholder = placeholder()
        self.content = content
    }

    var body: some View {
        Group {
            if let uiImage = loader.image {
                content(Image(uiImage: uiImage))
                    .blur(radius: blurRadius)
                    .onReceive(loader.$isHighResLoaded) { loaded in
                        if loaded {
                            withAnimation(.easeOut(duration: 0.4)) {
                                blurRadius = 0
                                showHighRes = true
                            }
                        }
                    }
            } else {
                placeholder
            }
        }
        .cornerRadius(10)
        .onAppear {
            loader.load()
        }
    }
}
