import SwiftUI

struct MarkerInfoSheetView: View {
    var spot: Spot?
    var viewModel = MarkerInfoSheetViewModel()
    
    @Binding var currentDetent: PresentationDetent
    @Binding var showDetails: Bool
    @Binding var showSheet: Bool
    @State private var isExpanded: Bool = false

    var body: some View {
        if let spot = spot {
            ZStack {
                VStack(alignment: .leading) {
                    if isExpanded {
                        
                        VStack(alignment: .leading, spacing: 8) {
                            Spacer()
                            Text(spot.title)
                                .font(.title2)
                                .foregroundColor(.primary)
                                .bold()
                            
                            Text(spot.description)
                                .font(.body)
                        }
                        .transition(.move(edge: .top).combined(with: .opacity))
                    }
                    VStack {
                        TabView {
                            ForEach(spot.pictures, id: \.self) { picture in
                                AsyncImage(url: viewModel.getSpotImage(id: picture)) { image in
                                    image
                                        .resizable()
                                        .aspectRatio(contentMode: isExpanded ? .fit : .fill)
                                        .frame(maxWidth: .infinity)
                                        .cornerRadius(10)
                                } placeholder: {
                                    SkeletonLoader()
                                        .frame(width: viewModel.getScreenWidth(), height: viewModel.getScreenWidth())
                                }
                            }
                        }
                        .tabViewStyle(PageTabViewStyle())
                    }.frame(height: isExpanded ? viewModel.getScreenHeight() : nil)
                    
                    if isExpanded {
                        VStack(alignment: .leading, spacing: 8) {
                            Text("created_by".localized() + spot.user)
                                .font(.caption)
                                .foregroundColor(.gray)
                            
                            Rating(stars: spot.rating, starSize: 25)
                            
                            if !spot.attributes.isEmpty {
                                Text("attributes".localized())
                                    .bold()
                                ForEach(spot.attributes, id: \.self) { attr in
                                    HStack {
                                        AsyncImage(url: viewModel.getAttributeImage(id: attr.id)) { image in
                                            image
                                                .renderingMode(.template)
                                                .resizable()
                                                .aspectRatio(contentMode: .fit)
                                                .frame(width: 20, height: 20)
                                                .foregroundColor(Color(hex: attr.color))
                                        } placeholder: {
                                            SkeletonLoader()
                                                .frame(width: 20, height: 20)
                                        }
                                        Text(attr.name)
                                        Spacer()
                                    }
                                }
                            }
                            HStack {
                                Spacer()
                                CustomButton(iconName: "chevron.right.circle.fill", buttonText: "details".localized(), action: {
                                    showDetails = true
                                    showSheet = false
                                })
                                Spacer()
                            }
                        }
                        .transition(.move(edge: .bottom).combined(with: .opacity))
                    }
                }
                .padding(16)
                .onChange(of: currentDetent) { oldValue, newValue in
                    withAnimation(.easeInOut(duration: 0.4)) {
                        isExpanded = newValue == .large
                    }
                }
            }
        }
    }
}
