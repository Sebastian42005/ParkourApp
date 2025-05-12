import SwiftUI
import Combine

struct UploadSpotView: View {
    @StateObject var viewModel: UploadSpotViewModel
    @State private var isCameraPickerPresented = false
    @State private var isGalleryPickerPresented = false
    @State private var isImageSourcePickerPresented = false
    @State private var keyboardHeight: CGFloat = 0

    var body: some View {
        ScrollView {
            VStack {
                // Titel
                CustomTextField(text: $viewModel.title, placeHolder: "title".localized())
                    .padding(.vertical, 5)
                
                // Beschreibung
                CustomTextField(text: $viewModel.spotDescription, placeHolder: "description".localized())
                    .padding(.vertical, 5)

                // Sportwahl Menü
                SelectDropdown(
                    options: viewModel.sportOptions,
                    selectedItem: $viewModel.selectedSport,
                    displayText: { $0.name },
                    placeholder: "select_a_sport".localized()
                )

                // Multi Select Dropdown
                Group {
                    if viewModel.selectedSport != nil {
                        MultiSelectDropdown(
                            options: viewModel.sportAttributes,
                            selectedItems: $viewModel.selectedAttributes,
                            displayText: { $0.name },
                            placeholder: "attributes".localized()
                        )
                        .padding()
                        .transition(.move(edge: .top).combined(with: .opacity))
                    }
                }
                .animation(.easeInOut(duration: 0.3), value: viewModel.selectedSport)


                Button(action: {
                    viewModel.showLocationPicker.toggle()
                }) {
                    HStack {
                        Image(systemName: "mappin.and.ellipse")
                            .font(.headline)
                        Text(viewModel.locationPickerText)
                            .font(.headline)
                            .lineLimit(1)
                            .truncationMode(.tail)
                        Spacer()
                        Image(systemName: "chevron.right")
                            .foregroundColor(.gray)
                    }
                    .padding()
                    .background(Color.gray.opacity(0.1))
                    .cornerRadius(12)
                }
                .padding(.horizontal)

                // Anzeigen der Bilder
                if !viewModel.capturedImages.isEmpty {
                    TabView {
                        ForEach(viewModel.capturedImages.indices, id: \.self) { index in
                            ZStack(alignment: .topTrailing) {
                                Image(uiImage: viewModel.capturedImages[index])
                                    .resizable()
                                    .scaledToFill()
                                    .frame(maxWidth: .infinity, maxHeight: 300)
                                    .clipped()

                                Button(action: {
                                    viewModel.removeImage(at: index)
                                }) {
                                    Image(systemName: "trash")
                                        .foregroundColor(.red)
                                        .padding()
                                }
                            }
                        }
                    }
                    .tabViewStyle(PageTabViewStyle())
                    .frame(height: 300)
                } else {
                    Text("take_photos".localized())
                        .foregroundColor(.gray)
                        .font(.headline)
                        .padding()
                }

                // Button für Bildquelle (Kamera oder Galerie)
                CustomButton(iconName: "camera", buttonText: "select_image_source".localized(), fullWidth: true, action: {
                    isImageSourcePickerPresented.toggle()
                })

                var isUploadDisabled: Bool {
                    viewModel.title.isEmpty ||
                    viewModel.spotDescription.isEmpty ||
                    viewModel.selectedSport == nil ||
                    viewModel.capturedImages.isEmpty
                }

                Button(action: {
                    viewModel.uploadSpot()
                }) {
                    Text("upload_spot".localized())
                        .font(.headline)
                        .padding()
                        .frame(maxWidth: .infinity)
                        .background(viewModel.isUploading || isUploadDisabled ? Color.gray : Color.primary)
                        .foregroundColor(.white)
                        .cornerRadius(10)
                        .opacity(viewModel.isUploading || isUploadDisabled ? 0.6 : 1.0)
                }
                .disabled(viewModel.isUploading || isUploadDisabled)
                .padding()

                .actionSheet(isPresented: $isImageSourcePickerPresented) {
                    ActionSheet(title: Text("choose_image_source".localized()), buttons: [
                        .default(Text("take_photo".localized())) {
                            isCameraPickerPresented.toggle()
                        },
                        .default(Text("pick_from_gallery".localized())) {
                            isGalleryPickerPresented.toggle()
                        },
                        .cancel()
                    ])
                }

                .sheet(isPresented: $isCameraPickerPresented) {
                    ImagePicker(didFinishPicking: { image in
                        viewModel.addImage(image)
                    }, isImagePickerPresented: $isCameraPickerPresented)
                }

                .sheet(isPresented: $isGalleryPickerPresented) {
                    GalleryPicker(didFinishPicking: { images in
                            for image in images {
                                viewModel.addImage(image)
                            }
                        }, isImagePickerPresented: $isGalleryPickerPresented)
                }
            }
            .padding()
            .onReceive(Publishers.keyboardHeight) { height in
                self.keyboardHeight = height
            }
            .padding(.bottom, keyboardHeight)
            .sheet(isPresented: $viewModel.showLocationPicker) {
                LocationPicker(
                    selectedLocation: $viewModel.location,
                    isPresented: $viewModel.showLocationPicker,
                    title: viewModel.title,
                    sport: viewModel.selectedSport
                )
            }
            .gesture(
                TapGesture()
                    .onEnded { _ in
                        // Tastatur schließen, wenn auf den Bildschirm außerhalb eines TextFields getippt wird
                        hideKeyboard()
                    }
            )
        }
    }

    // Funktion zum Schließen der Tastatur
    private func hideKeyboard() {
        UIApplication.shared.endEditing()
    }
}


// Eine Extension zur Erfassung der Tastaturhöhe
extension Publishers {
    static var keyboardHeight: AnyPublisher<CGFloat, Never> {
        Publishers.Merge(
            NotificationCenter.default.publisher(for: UIResponder.keyboardWillShowNotification)
                .map { (notification) -> CGFloat in
                    let userInfo = notification.userInfo!
                    let frame = userInfo[UIResponder.keyboardFrameEndUserInfoKey] as! CGRect
                    return frame.height
                },
            NotificationCenter.default.publisher(for: UIResponder.keyboardWillHideNotification)
                .map { _ in CGFloat(0) }
        )
        .eraseToAnyPublisher()
    }
}

// Eine kleine Extension, um das Beenden der Bearbeitung zu vereinfachen
extension UIApplication {
    func endEditing() {
        windows.first?.endEditing(true)
    }
}

struct UploadSpotViewView_Previews: PreviewProvider {
    static var previews: some View {
        PreviewWrapper()
    }
    
    struct PreviewWrapper: View {
        @State private var showView = true
        
        var body: some View {
            UploadSpotView(viewModel: UploadSpotViewModel(showView: $showView))
        }
    }
}
