import SwiftUI

struct RatingPickerDialog: View {
    @ObservedObject var viewModel: RatingPickerDialogViewModel

    var body: some View {
        VStack(spacing: 10) {
            RatingPicker(rating: $viewModel.stars, starSize: viewModel.starSize)
            CustomTextField(text: $viewModel.description, placeHolder: "Description")
            CustomButton(iconName: "star.circle.fill", buttonText: "Rate", action: {
                viewModel.uploadRating()
            })
        }
        .padding(.horizontal, 10)
    }

    static func roundToHalf(_ value: Int) -> Double {
        return (Double(value) * 2).rounded() / 2
    }
}

#Preview {
    RatingPickerDialog(
        viewModel: RatingPickerDialogViewModel(starSize: 3, spotId: 1)
    )
}

