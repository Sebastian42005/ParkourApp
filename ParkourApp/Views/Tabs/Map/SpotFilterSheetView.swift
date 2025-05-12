import SwiftUI

struct SpotFilterSheetView: View {
    @ObservedObject var viewModel: SpotFilterSheetViewModel

    var body: some View {
        ScrollView {
            VStack(spacing: 20) {
                Text("filter".localized())
                    .font(.largeTitle.bold())
                    .foregroundColor(.primary)
                    .padding(.top)
                
                VStack(spacing: 16) {
                    SelectDropdown(
                        options: viewModel.sports,
                        selectedItem: $viewModel.selectedSport,
                        displayText: { $0.name },
                        placeholder: "select_sport".localized()
                    )
                    
                    if viewModel.selectedSport != nil {
                        MultiSelectDropdown(
                            options: viewModel.attributes,
                            selectedItems: $viewModel.selectedAttributes,
                            displayText: { $0.name },
                            placeholder: "attribute_select".localized()
                        )
                    }
                    
                    RatingPicker(rating: $viewModel.selectedRating, starSize: 30)
                    
                    SelectDropdown(
                        options: viewModel.cities,
                        selectedItem: $viewModel.selectedCity,
                        displayText: { $0 },
                        placeholder: "select_city".localized()
                    )
                }
                .padding()
                .background(Color(UIColor.secondarySystemBackground))
                .cornerRadius(12)
                .shadow(color: Color.black.opacity(0.05), radius: 5, x: 0, y: 3)
                .padding(.horizontal)
                
                HStack(spacing: 5) {
                    CustomButton(iconName: "arrow.counterclockwise", buttonText: "reset".localized(), backgroundColor: .gray.opacity(0.2), textColor: .primary) {
                        viewModel.resetFilters()
                    }
                    
                    CustomButton(iconName: "line.3.horizontal.decrease.circle", buttonText: "apply".localized(), backgroundColor: .primary, textColor: .white) {
                        viewModel.applyFilter()
                    }
                }
                .padding(.top, 10)
            }
            .padding()
        }
    }
}

#Preview {
    SpotFilterSheetView(viewModel: SpotFilterSheetViewModel(viewModel: MapTabViewModel()))
}
