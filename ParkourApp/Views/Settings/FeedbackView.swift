import SwiftUI

struct FeedbackView: View {
    @Environment(\.dismiss) var dismiss
    @ObservedObject private var viewModel = FeedbackViewModel()

    var body: some View {
        NavigationStack {
            VStack(alignment: .leading, spacing: 16) {
                // Feedback Header
                Text("share_feedback".localized())
                    .font(.headline)

                // Feedback-Typ Auswahl
                SelectDropdown(
                    options: FeedbackType.allCases,
                    selectedItem: $viewModel.selectedFeedbackType,
                    displayText: { $0.label },
                    placeholder: "select_feedback_type".localized()
                )

                // Feedback-Text
                TextEditor(text: $viewModel.feedbackText)
                    .frame(height: 200)
                    .padding(4)
                    .overlay(
                        RoundedRectangle(cornerRadius: 8)
                            .stroke(Color.gray.opacity(0.3), lineWidth: 1)
                    )

                Button("send".localized()) {
                    viewModel.uploadFeedback()
                }
                .disabled(viewModel.selectedFeedbackType == nil || viewModel.feedbackText.trimmingCharacters(in: .whitespacesAndNewlines).isEmpty || viewModel.isLoading)
                .buttonStyle(.borderedProminent)

                Spacer()
            }
            .padding()
            .navigationTitle("feedback".localized())
            .toolbar {
                ToolbarItem(placement: .cancellationAction) {
                    Button("cancel".localized()) {
                        dismiss()
                    }
                }
            }
            .onReceive(viewModel.$didUploadFeedback) { uploaded in
                if uploaded {
                    dismiss()
                }
            }
        }
    }
}

#Preview {
    FeedbackView()
}
