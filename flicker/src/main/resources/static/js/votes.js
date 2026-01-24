// Vote System with AJAX
document.addEventListener('DOMContentLoaded', function () {
    const voteBtns = document.querySelectorAll('.vote-btn');
    const csrfToken = document.querySelector('input[name="_csrf"]')?.value;

    voteBtns.forEach(btn => {
        btn.addEventListener('click', function (e) {
            e.preventDefault();
            e.stopPropagation();

            const postId = this.dataset.postId;
            const userId = this.dataset.userId; // Ensure this is set in HTML
            const isUpvote = this.classList.contains('upvote');
            const url = isUpvote
                ? `/reader/post/${postId}/upvote`
                : `/reader/post/${postId}/downvote`;

            // Optimistic UI Update
            const voteContainer = this.closest('.post-actions');
            const upvoteBtn = voteContainer.querySelector('.upvote');
            const downvoteBtn = voteContainer.querySelector('.downvote');
            const upvoteCount = voteContainer.querySelector('.upvote-count');
            const downvoteCount = voteContainer.querySelector('.downvote-count');

            let upChange = 0;
            let downChange = 0;

            if (isUpvote) {
                if (this.classList.contains('voted')) {
                    // Removing upvote
                    upChange = -1;
                } else {
                    // Adding upvote
                    upChange = 1;
                    if (downvoteBtn.classList.contains('voted')) {
                        // Switching from down to up
                        downChange = -1;
                    }
                }
            } else {
                if (this.classList.contains('voted')) {
                    // Removing downvote
                    downChange = -1;
                } else {
                    // Adding downvote
                    downChange = 1;
                    if (upvoteBtn.classList.contains('voted')) {
                        // Switching from up to down
                        upChange = -1;
                    }
                }
            }

            // Perform Fetch
            fetch(url, {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/x-www-form-urlencoded',
                    'X-CSRF-TOKEN': csrfToken
                },
                body: `userId=${userId}`
            }).then(async response => {
                if (response.ok) {
                    // Update UI classes on success (or keep optimistic)
                    if (this.classList.contains('voted')) {
                        this.classList.remove('voted');
                    } else {
                        if (isUpvote) {
                            downvoteBtn.classList.remove('voted');
                            upvoteBtn.classList.add('voted');
                        } else {
                            upvoteBtn.classList.remove('voted');
                            downvoteBtn.classList.add('voted');
                        }
                    }
                    updateVoteCount(upvoteCount, upChange);
                    updateVoteCount(downvoteCount, downChange);
                } else {
                    console.error('Vote failed:', await response.text());
                    // Revert optimistic update (simple reload or reversal logic could go here)
                    alert("Failed to vote. Please try again.");
                }
            }).catch(error => console.error('Error:', error));
        });
    });
});

function updateVoteCount(element, change) {
    if (!element) return;
    const current = parseInt(element.textContent) || 0;
    element.textContent = current + change;

    // Add pulse animation
    element.style.transform = 'scale(1.2)';
    setTimeout(() => {
        element.style.transform = 'scale(1)';
    }, 200);
}
