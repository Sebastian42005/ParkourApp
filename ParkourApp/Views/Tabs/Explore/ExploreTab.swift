import SwiftUI

struct ExploreTab: View {
    let columns = [
           GridItem(.flexible()),
           GridItem(.flexible())
       ]
    @ObservedObject var viewModel = ExploreTabViewModel()

    var body: some View {
        if !viewModel.isLoading {
            ScrollView {
                LazyVGrid(columns: [GridItem].init(repeating: GridItem(.flexible()), count: 3)) {
                    ForEach(viewModel.spots, id: \.self) { spot in
                        AsyncImage(url: viewModel.getSpotImage(id: spot.pictures.first!)) { image in
                            image
                                .resizable()
                                .scaledToFill()
                                .frame(width: viewModel.getScreenWidth(), height: viewModel.getScreenWidth())
                                .clipped()
                                .cornerRadius(10)
                        } placeholder: {
                            ProgressView()
                                .frame(width: 150, height: 150)
                        }
                    }
                }
            }
            .padding(20)
        } else {
            ProgressView()
        }
    }
}

#Preview {
    ExploreTab()
}
