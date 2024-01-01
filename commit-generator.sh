#!/bin/bash

# Android-specific commit messages
ANDROID_COMMITS=(
    "Implement ViewModel for MainActivity"
    "Refactor Dagger-Hilt modules"
    "Fix RecyclerView adapter flicker"
    "Add Room database migration"
    "Update Gradle wrapper to 8.3"
    "Implement Jetpack Compose navigation"
    "Fix memory leak in Bitmap cache"
    "Add Firebase Crashlytics reporting"
    "Optimize ConstraintLayout hierarchy"
    "Fix foreground service notification"
    "Migrate to Material Design 3"
    "Add WorkManager sync scheduler"
    "Fix window insets handling"
    "Update targetSdkVersion to 34"
    "Implement dark mode support"
    "Add Espresso UI tests"
    "Fix fragment transaction state"
    "Optimize ProGuard rules"
    "Add KSP processing for Room"
    "Implement Dynamic Feature module"
    "Fix keyboard overlap issue"
    "Update Retrofit to 2.9.0"
    "Add MotionLayout transitions"
    "Fix Android 14 background limits"
    "Implement biometric auth"
    "Add baseline profiles"
    "Fix lint warnings"
    "Update compose-compiler to 1.5.3"
    "Implement in-app updates"
    "Fix shared element transition"
)

# Initialize realistic Android repo
if [ ! -d .git ]; then
    git init
    mkdir -p app/src/{main/java/com/example,res/layout}
    touch \
        .gitignore \
        build.gradle \
        app/build.gradle \
        app/src/main/AndroidManifest.xml \
        app/src/main/res/values/strings.xml \
        app/src/main/java/com/example/MainActivity.kt
    
    cat > app/build.gradle <<EOL
plugins {
    id 'com.android.application'
    id 'org.jetbrains.kotlin.android'
}
EOL

    git add .
    GIT_AUTHOR_DATE="2023-12-31 09:00:00" \
    GIT_COMMITTER_DATE="2023-12-31 09:00:00" \
    git commit -m "Initial Android project setup" 
fi

START_DATE="2024-01-01"
END_DATE="2025-01-01"

CURRENT=$(date -d "$START_DATE" +%s)
END=$(date -d "$END_DATE" +%s)

while [ "$CURRENT" -le "$END" ]; do
    CURRENT_DATE=$(date -d "@$CURRENT" +"%Y-%m-%d")
    COMMITS=$(( (RANDOM % 16) + 5 ))  # 5-20 commits
    
    echo "📅 Generating $COMMITS Android commits for $CURRENT_DATE"
    
    for ((i=1; i<=COMMITS; i++)); do
        # Random time
        HOUR=$(printf "%02d" $(( RANDOM % 24 )))
        MINUTE=$(printf "%02d" $(( RANDOM % 60 )))
        SECOND=$(printf "%02d" $(( RANDOM % 60 )))
        FULL_DATE="${CURRENT_DATE} ${HOUR}:${MINUTE}:${SECOND}"
        
        # Random Android file change
        ANDROID_FILES=(
            "app/build.gradle"
            "app/src/main/res/values/strings.xml"
            "app/src/main/java/com/example/MainActivity.kt"
            "app/src/main/res/layout/activity_main.xml"
        )
        FILE_TO_MODIFY=${ANDROID_FILES[$RANDOM % ${#ANDROID_FILES[@]}]}
        
        # Make realistic-looking change
        echo "// Auto-generated at ${FULL_DATE}" >> $FILE_TO_MODIFY
        git add $FILE_TO_MODIFY
        
        # Random commit message
        COMMIT_MSG=${ANDROID_COMMITS[$RANDOM % ${#ANDROID_COMMITS[@]}]}
        
        # Commit
        GIT_AUTHOR_DATE="$FULL_DATE" \
        GIT_COMMITTER_DATE="$FULL_DATE" \
        git commit -m "$COMMIT_MSG" --quiet
    done
    
    CURRENT=$(date -d "@$CURRENT + 1 day" +%s)
done

# Push to remote (use with caution!)
git push origin main --force